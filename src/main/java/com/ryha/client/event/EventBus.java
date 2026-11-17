package com.ryha.client.event;

import com.ryha.client.util.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple synchronous event bus.
 * Listeners are stored per event type. Posting null is a no-op.
 */
public class EventBus {

    private final Map<Class<?>, List<EventListener<?>>> listeners = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <E extends Event> void register(Class<E> type, EventListener<E> listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> void unregister(Class<E> type, EventListener<E> listener) {
        List<EventListener<?>> list = listeners.get(type);
        if (list != null) list.remove(listener);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <E extends Event> void post(E event) {
        if (event == null) return;
        List<EventListener<?>> list = listeners.get(event.getClass());
        if (list == null || list.isEmpty()) return;
        for (EventListener l : new ArrayList<>(list)) {
            try {
                l.onEvent(event);
            } catch (Exception ex) {
                Logger.error("EventBus exception in listener for " + event.getClass().getSimpleName(), ex);
            }
        }
    }

    /** Unregister all annotated listener methods that were registered from an object. */
    public void unregisterObject(Object obj) {
        // Remove all lambdas whose enclosing instance is obj — we can't match lambdas
        // directly, so we clear the whole listener list and re-register everything except
        // the removed object. Since modules register once and rarely unregister, the
        // simplest safe approach is to remove entries by identity using a wrapper Map.
        // For this codebase, we track objects in a separate set and clear by object identity.
        listeners.forEach((type, list) ->
            list.removeIf(l -> {
                // Lambdas created by registerObject capture a Method + obj; we can't
                // introspect the lambda. Instead we store a marker class that wraps it.
                return l instanceof ObjectBoundListener obl && obl.owner == obj;
            })
        );
    }

    /** Register all annotated listener methods on an object (simple reflection approach). */
    public void registerObject(Object obj) {
        for (var method : obj.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Subscribe.class)) continue;
            if (method.getParameterCount() != 1) continue;
            Class<?> paramType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(paramType)) continue;
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            Class<Event> eventType = (Class<Event>) paramType;
            ObjectBoundListener<Event> wrapper = new ObjectBoundListener<>(obj, event -> {
                try { method.invoke(obj, event); }
                catch (Exception ex) { Logger.error("EventBus reflection dispatch error", ex); }
            });
            register(eventType, wrapper);
        }
    }

    /** Wrapper that ties a listener to its owning object so we can unregister by object. */
    private static final class ObjectBoundListener<E extends Event> implements EventListener<E> {
        final Object owner;
        private final EventListener<E> delegate;
        ObjectBoundListener(Object owner, EventListener<E> delegate) {
            this.owner    = owner;
            this.delegate = delegate;
        }
        @Override public void onEvent(E event) { delegate.onEvent(event); }
    }
}
