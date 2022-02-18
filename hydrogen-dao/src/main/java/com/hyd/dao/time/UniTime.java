package com.hyd.dao.time;

import java.io.Serializable;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * UniTime 是对日期时间封装，包含以下特性：
 * 1、值不可变，线程安全；
 * 2、与其它类型（Date/Calendar/LocalDateTime/String）自由转换；
 * 3、可方便的进行加减操作、计算差值；
 * 4、可按秒、按分、按小时、按天取整；
 * 5、可序列化和反序列化；
 * 6、可按指定时间间隔遍历将来的时间点。
 * <p>
 * 针对标准的 java.time API 规避了以下问题：
 * 1、DateTimeFormatter 无法将不带时间部分的字符串解析成完整的 LocalDateTime 对象，这里补完一个零点的时刻即可；
 * 2、Temporal 的 plus() 和 minus() 方法只接受非负数，这里合并成一个可接受正数和负数的方法。
 */
public final class UniTime implements Comparable<UniTime>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 默认时区
     */
    public static final ZoneId ZONE = ZoneId.systemDefault();

    /**
     * 表示零时零分零秒的时间（不含日期部分）
     */
    public static final LocalTime DAY_START = LocalTime.of(0, 0, 0, 0);

    /**
     * 表示一天当中最后一秒的时间（不含日期部分）
     */
    public static final LocalTime DAY_END = LocalTime.of(23, 59, 59, 0);

    ////////////////////////////////////////////////////////////// 静态构造方法

    /**
     * 取当前时间
     */
    public static UniTime now() {
        return new UniTime(ZonedDateTime.now());
    }

    /**
     * 从 Date 对象转化
     */
    public static UniTime fromDate(Date date) {
        return new UniTime(ZonedDateTime.ofInstant(date.toInstant(), ZONE));
    }

    /**
     * 从 Calendar 对象转化
     */
    public static UniTime fromCalendar(Calendar calendar) {
        if (calendar instanceof GregorianCalendar) {
            return fromTemporal(((GregorianCalendar) calendar).toZonedDateTime());
        } else {
            return fromTemporal(calendar.toInstant());
        }
    }

    /**
     * 从 Temporal 对象转化。如果对象中没有时区，则取默认时区
     */
    public static UniTime fromTemporal(Temporal temporal) {

        if (temporal instanceof ZonedDateTime) {
            return new UniTime((ZonedDateTime) temporal);
        }

        // 年月日的最小值都是 1，时分秒的最小值都是 0
        LocalDateTime localDateTime = LocalDateTime.of(
            temporal.isSupported(ChronoField.YEAR) ? temporal.get(ChronoField.YEAR) : 1,
            temporal.isSupported(ChronoField.MONTH_OF_YEAR) ? temporal.get(ChronoField.MONTH_OF_YEAR) : 1,
            temporal.isSupported(ChronoField.DAY_OF_MONTH) ? temporal.get(ChronoField.DAY_OF_MONTH) : 1,
            temporal.isSupported(ChronoField.HOUR_OF_DAY) ? temporal.get(ChronoField.HOUR_OF_DAY) : 0,
            temporal.isSupported(ChronoField.MINUTE_OF_HOUR) ? temporal.get(ChronoField.MINUTE_OF_HOUR) : 0,
            temporal.isSupported(ChronoField.SECOND_OF_MINUTE) ? temporal.get(ChronoField.SECOND_OF_MINUTE) : 0,
            temporal.isSupported(ChronoField.NANO_OF_SECOND) ? temporal.get(ChronoField.NANO_OF_SECOND) : 0
        );

        if (temporal instanceof ChronoZonedDateTime) {
            ZoneId zone = ((ChronoZonedDateTime<?>) temporal).getZone();
            return new UniTime(localDateTime.atZone(zone));
        } else {
            return new UniTime(localDateTime.atZone(ZONE));
        }
    }

    /**
     * 从字符串转化
     *
     * @param value    表示时间的字符串
     * @param pattern  首选格式
     * @param patterns 其他可选格式
     */
    public static UniTime parse(String value, String pattern, String... patterns) {

        String[] finalPatterns;
        if (patterns != null) {
            finalPatterns = new String[patterns.length + 1];
            finalPatterns[0] = pattern;
            System.arraycopy(patterns, 0, finalPatterns, 1, patterns.length);
        } else {
            finalPatterns = new String[]{pattern};
        }

        DateTimeParseException parseException = null;
        for (String p : finalPatterns) {
            try {
                DateTimeFormatter formatter = TimeFormatters.ofPattern(p);
                TemporalAccessor accessor = formatter.parse(value);

                return new UniTime(LocalDateTime.of(
                    getDatePart(accessor, LocalDate.now()),
                    getTimePart(accessor, DAY_START)
                ).atZone(ZONE));
            } catch (DateTimeParseException e) {
                parseException = e;
            }
        }

        throw parseException;
    }

    private static LocalDate getDatePart(TemporalAccessor ta, LocalDate defaultValue) {
        LocalDate date = ta.query(TemporalQueries.localDate());
        return date == null ? defaultValue : date;
    }

    private static LocalTime getTimePart(TemporalAccessor ta, LocalTime defaultValue) {
        LocalTime time = ta.query(TemporalQueries.localTime());
        return time == null ? defaultValue : time;
    }

    ////////////////////////////////////////////////////////////// 成员和构造方法

    private final ZonedDateTime value;

    public UniTime(ZonedDateTime value) {
        this.value = value;
    }

    public ZonedDateTime getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "UnifiedTime{" +
            "value=" + value +
            '}';
    }

    ////////////////////////////////////////////////////////////// 转换成其他类型

    public Date toDate() {
        return Date.from(this.value.toInstant());
    }

    public Calendar toCalendar() {
        return GregorianCalendar.from(this.value);
    }

    public LocalDateTime toLocalDateTime() {
        return this.value.toLocalDateTime();
    }

    public Instant toInstant() {
        return this.value.toInstant();
    }

    public long getEpochMillis() {
        return toDate().getTime();
    }

    public long getEpochSecond() {
        return this.value.toEpochSecond();
    }

    public String format(String pattern) {
        return TimeFormatters.ofPattern(pattern).format(value);
    }

    //////////////////////////////////////////////////////////////

    public UniTime addYears(int count) {
        return this.add(ChronoUnit.YEARS, count);
    }

    public UniTime addMonths(int count) {
        return this.add(ChronoUnit.MONTHS, count);
    }

    public UniTime addDays(int count) {
        return this.add(ChronoUnit.DAYS, count);
    }

    public UniTime addHours(int count) {
        return this.add(ChronoUnit.HOURS, count);
    }

    public UniTime addMinutes(int count) {
        return this.add(ChronoUnit.MINUTES, count);
    }

    public UniTime addSeconds(int count) {
        return this.add(ChronoUnit.SECONDS, count);
    }

    public UniTime addMillis(int count) {
        return this.add(ChronoUnit.MILLIS, count);
    }

    public UniTime addNanos(int count) {
        return this.add(ChronoUnit.NANOS, count);
    }

    //////////////////////////////////////////////////////////////

    public int get(ChronoField unit) {
        return this.value.get(unit);
    }

    public int getYear() {
        return this.value.getYear();
    }

    public Month getMonth() {
        return this.value.getMonth();
    }

    public int getMonthValue() {
        return this.value.getMonthValue();
    }

    public int getDayOfMonth() {
        return this.value.getDayOfMonth();
    }

    public int getDayOfYear() {
        return this.value.getDayOfYear();
    }

    public DayOfWeek getDayOfWeek() {
        return this.value.getDayOfWeek();
    }

    public int getWeekOfYear() {
        return this.value.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
    }

    public int getHour() {
        return this.value.getHour();
    }

    public int getMinute() {
        return this.value.getMinute();
    }

    public int getSecond() {
        return this.value.getSecond();
    }

    public int getMilliSecond() {
        return this.value.getNano() / 1000000;
    }

    public int getNanoSecond() {
        return this.value.getNano();
    }

    // 返回当前月份的天数
    public int lengthOfMonth() {
        YearMonth ym = YearMonth.from(this.value);
        return ym.lengthOfMonth();
    }

    // 返回当前年份的天数
    public int lengthOfYear() {
        YearMonth ym = YearMonth.from(this.value);
        return ym.lengthOfYear();
    }

    // 添加一个相对值，得到另一个 UnifiedTime 对象
    public UniTime add(ChronoUnit unit, long amount) {
        return amount == 0 ? this :
            amount > 0 ? new UniTime(this.value.plus(amount, unit)) :
                new UniTime(this.value.minus(-amount, unit));
    }

    // 减去一个相对值，得到另一个 UnifiedTime 对象
    public UniTime minus(ChronoUnit unit, long amount) {
        return add(unit, -amount);
    }

    // 添加一个时间段，得到另一个 UnifiedTime 对象
    public UniTime add(TemporalAmount amount) {
        return new UniTime(this.value.plus(amount));
    }

    // 减去一个时间段，得到另一个 UnifiedTime 对象
    public UniTime minus(TemporalAmount amount) {
        return new UniTime(this.value.minus(amount));
    }

    // 调整一个绝对值，得到另一个 UnifiedTime 对象
    public UniTime with(TemporalAdjuster adjuster) {
        return new UniTime(this.value.with(adjuster));
    }

    // 调整一个属性，得到另一个 UnifiedTime 对象
    public UniTime with(ChronoField field, long newValue) {
        return new UniTime(this.value.with(field, newValue));
    }

    // 计算两个 UnifiedTime 之间的时差，以 unit 为单位
    public long until(UniTime item, ChronoUnit unit) {
        return this.value.until(item.value, unit);
    }

    //////////////////////////////////////////////////////////////

    /**
     * 删除天以下部分
     */
    public UniTime trimToDay() {
        return new UniTime(this.value.with(DAY_START));
    }

    /**
     * 删除小时以下部分
     */
    public UniTime trimToHour() {
        return new UniTime(
            this.value
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
                .with(ChronoField.MINUTE_OF_HOUR, 0)
        );
    }

    /**
     * 删除分钟以下部分
     */
    public UniTime trimToMinute() {
        return new UniTime(
            this.value
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
        );
    }

    /**
     * 删除秒以下部分
     */
    public UniTime trimToSecond() {
        if (this.value.get(ChronoField.MILLI_OF_SECOND) == 0) {
            return this;
        } else {
            return new UniTime(this.value.with(ChronoField.MILLI_OF_SECOND, 0));
        }
    }

    //////////////////////////////////////////////////////////////

    public boolean withinTheSameDay(UniTime other) {
        return this.value.toLocalDate().equals(other.value.toLocalDate());
    }

    /**
     * 从当前时间开始，按指定间隔时间循环多少次
     */
    public Stream<UniTime> iterateTimes(int count, TemporalAmount amount) {
        Iterator<UniTime> iterator = new Iterator<UniTime>() {

            private UniTime current = UniTime.this.minus(amount);

            private int counter = count;

            @Override
            public boolean hasNext() {
                return counter > 0;
            }

            @Override
            public UniTime next() {
                counter -= 1;
                current = current.add(amount);
                return current;
            }
        };

        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(iterator, Spliterator.NONNULL), false
        );
    }

    /**
     * 从当前时间开始，按指定间隔时间循环直到结束时间
     */
    public Stream<UniTime> iterateUntil(UniTime until, TemporalAmount amount) {
        Iterator<UniTime> iterator = new Iterator<UniTime>() {

            private UniTime current = UniTime.this.minus(amount);

            @Override
            public boolean hasNext() {
                return current.add(amount).compareTo(until) <= 0;
            }

            @Override
            public UniTime next() {
                current = current.add(amount);
                return current;
            }
        };

        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(iterator, Spliterator.NONNULL), false
        );
    }

    //////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        UniTime uniTime = (UniTime) object;
        return Objects.equals(value, uniTime.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(UniTime o) {
        return this.value.compareTo(o.value);
    }
}
