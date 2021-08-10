import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public class LinuxExpression {
	private final CronField[] fields;
	private final String expression;


	public LinuxExpression(CronField dayOfWeek, CronField month, CronField daysOfMonth, CronField hour, CronField minute, String expression) {
		this.fields = new CronField[]{dayOfWeek, month, daysOfMonth, hour, minute, CronField.zeroNanos()};
		this.expression = expression;
	}

	@Override
	public String toString() {
		return this.expression;
	}

	public static LinuxExpression parse(String expression) {
		String[] fields = StringUtils.tokenizeToStringArray(expression, " ");

		CronField minutes = CronField.parseMinutes(fields[0]);
		CronField hours = CronField.parseHours(fields[1]);
		CronField daysOfMonth = CronField.parseDaysOfMonth(fields[2]);
		CronField months = CronField.parseMonth(fields[3]);
		CronField daysOfWeek = CronField.parseDaysOfWeek(fields[4]);


		return new LinuxExpression(
				minutes,
				hours,
				daysOfMonth,
				months,
				daysOfWeek,
				expression
		);
	}

	@Nullable
	public <T extends Temporal & Comparable<? super T>> T next(T temporal) {
		return nextOrSame(ChronoUnit.NANOS.addTo(temporal, 1));
	}


	@Nullable
	private <T extends Temporal & Comparable<? super T>> T nextOrSame(T temporal) {
		for (int i = 0; i < 366; i++) {
			T result = nextOrSameInternal(temporal);
			if (result == null || result.equals(temporal)) {
				return result;
			}
			temporal = result;
		}
		return null;
	}

	@Nullable
	private <T extends Temporal & Comparable<? super T>> T nextOrSameInternal(T temporal) {
		for (CronField field : this.fields) {
			temporal = field.nextOrSame(temporal);
			if (temporal == null) {
				return null;
			}
		}
		return temporal;
	}

	public String getExpression() {
		return expression;
	}
}
