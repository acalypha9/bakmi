package com.kelompok6.bakmi;

import com.kelompok6.bakmi.Models.Customer;

/**
 * Session holds a process-wide reference to the currently active {@link Customer}.
 * <p>
 * This is a simple utility used to share the selected customer (DineIn/TakeAway/anonymous)
 * across different controllers and screens. It is intentionally minimal and not thread-safe —
 * it's suitable for single-threaded JavaFX usage.
 */
public final class Session {
    private static Customer currentCustomer;

    /**
     * Private constructor to prevent instantiation.
     */
    private Session() {}

    /**
     * Returns the currently stored {@link Customer} instance, or {@code null} if none is set.
     *
     * @return the current customer or {@code null}
     */
    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * Replaces the current customer with the provided instance.
     * Passing {@code null} is allowed and will clear the session's customer.
     *
     * @param c the customer to set as current, or {@code null} to clear
     */
    public static void setCurrentCustomer(Customer c) {
        currentCustomer = c;
    }

    /**
     * Clears the session by removing any stored customer.
     */
    public static void clear() {
        currentCustomer = null;
    }
}