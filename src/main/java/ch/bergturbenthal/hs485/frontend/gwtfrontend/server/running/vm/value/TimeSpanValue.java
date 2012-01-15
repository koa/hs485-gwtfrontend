package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value;

import java.util.concurrent.TimeUnit;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;

public class TimeSpanValue implements Value {
	private final int				count;
	private final TimeUnit	unit;

	public TimeSpanValue(final int count, final TimeUnit unit) {
		this.count = count;
		this.unit = unit;
	}

	public int convert(final TimeUnit targetUnit) {
		return (int) targetUnit.convert(count, unit);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TimeSpanValue other = (TimeSpanValue) obj;
		if (count != other.count)
			return false;
		if (unit != other.unit)
			return false;
		return true;
	}

	public int getCount() {
		return count;
	}

	public TimeUnit getUnit() {
		return unit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + (unit == null ? 0 : unit.hashCode());
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("TimeSpanValue [count=");
		builder.append(count);
		builder.append(", ");
		if (unit != null) {
			builder.append("unit=");
			builder.append(unit);
		}
		builder.append("]");
		return builder.toString();
	}

}
