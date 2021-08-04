import java.time.DateTimeException
import java.time.temporal.ChronoField
import java.time.temporal.Temporal
import java.time.temporal.ValueRange

enum Type {
    NANO(ChronoField.NANO_OF_SECOND),
    SECOND(ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
    MINUTE(ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
    HOUR(ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
    DAY_OF_MONTH(ChronoField.DAY_OF_MONTH, ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
    MONTH(ChronoField.MONTH_OF_YEAR, ChronoField.DAY_OF_MONTH, ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
    DAY_OF_WEEK(ChronoField.DAY_OF_WEEK, ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND);


    private final ChronoField field

    private final ChronoField[] lowerOrders


    Type(ChronoField field, ChronoField... lowerOrders) {
        this.field = field;
        this.lowerOrders = lowerOrders;
    }

    public int get(Temporal date) {
        return date.get(this.field);
    }

    /**
     * Return the general range of this type. For instance, this methods
     * will return 0-31 for {@link #MONTH}.
     * @return the range of this field
     */
    ValueRange range() {
        return this.field.range();
    }

    /**
     * Check whether the given value is valid, i.e. whether it falls in
     * {@linkplain #range() range}.
     * @param value the value to check
     * @return the value that was passed in
     * @throws IllegalArgumentException if the given value is invalid
     */
    int checkValidValue(int value) {
        if (this == DAY_OF_WEEK && value == 0) {
            value
        } else {
            try {
                this.field.checkValidIntValue(value);
            }
            catch (DateTimeException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Elapse the given temporal for the difference between the current
     * value of this field and the goal value. Typically, the returned
     * temporal will have the given goal as the current value for this type,
     * but this is not the case for {@link #DAY_OF_MONTH}.
     * @param temporal the temporal to elapse
     * @param goal the goal value
     * @param < T >  the type of temporal
     * @return the elapsed temporal, typically with {@code goal} as value
     * for this type.
     */
    def <T extends Temporal & Comparable<? super T>> T elapseUntil(T temporal, int goal) {
        int current = get(temporal);
        ValueRange range = temporal.range(this.field);
        if (current < goal) {
            if (range.isValidIntValue(goal)) {
                return cast(temporal.with(this.field, goal));
            } else {
                // goal is invalid, eg. 29th Feb, so roll forward
                long amount = range.getMaximum() - current + 1;
                return this.field.getBaseUnit().addTo(temporal, amount);
            }
        } else {
            long amount = goal + range.getMaximum() - current + 1 - range.getMinimum();
            return this.field.getBaseUnit().addTo(temporal, amount);
        }
    }

    /**
     * Roll forward the give temporal until it reaches the next higher
     * order field. Calling this method is equivalent to calling
     * {@link #elapseUntil(Temporal, int)} with goal set to the
     * minimum value of this field's range.
     * @param temporal the temporal to roll forward
     * @param < T >  the type of temporal
     * @return the rolled forward temporal
     */
    def <T extends Temporal & Comparable<? super T>> T rollForward(T temporal) {
        int current = get(temporal);
        ValueRange range = temporal.range(this.field);
        long amount = range.getMaximum() - current + 1;
        return this.field.getBaseUnit().addTo(temporal, amount);
    }

    /**
     * Reset this and all lower order fields of the given temporal to their
     * minimum value. For instance for {@link #MINUTE}, this method
     * resets nanos, seconds, <strong>and</strong> minutes to 0.
     * @param temporal the temporal to reset
     * @param < T >  the type of temporal
     * @return the reset temporal
     */
    def <T extends Temporal> T reset(T temporal) {
        for (ChronoField lowerOrder : this.lowerOrders) {
            if (temporal.isSupported(lowerOrder)) {
                temporal = lowerOrder.adjustInto(temporal, temporal.range(lowerOrder).getMinimum());
            }
        }
        return temporal;
    }

    @Override
    String toString() {
        return this.field.toString();
    }
}