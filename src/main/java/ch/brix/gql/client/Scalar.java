package ch.brix.gql.client;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

/**
 * Base for scalars
 */
@RequiredArgsConstructor
@Getter
public class Scalar {

    @NonNull
    private final String value;

    @Override
    public String toString() {
        return value;
    }

    /**
     * @param object requires correct implementation of toString()
     * @return Scalar
     */
    public Scalar from(Object object) {
        return new Scalar(object.toString());
    }

    public boolean getBoolean() {
        return Boolean.parseBoolean(value);
    }

    public int getInt() {
        return Integer.parseInt(value);
    }

    public byte getByte() {
        return Byte.parseByte(value);
    }

    public long getLong() {
        return Long.parseLong(value);
    }

    public short getShort() {
        return Short.parseShort(value);
    }

    public char getChar() {
        return value.charAt(0);
    }

    public BigInteger getBigInteger() {
        return new BigInteger(value);
    }

    public double getDouble() {
        return Double.parseDouble(value);
    }

    public float getFloat() {
        return Float.parseFloat(value);
    }

    public BigDecimal getBigDecimal() {
        return new BigDecimal(value);
    }

    public Date getDateFromUnixSeconds() {
        return new Date(Long.parseLong(value) * 1000L);
    }

    public Date getDateFromUnixMilis() {
        return new Date(Long.parseLong(value));
    }

    public Date getDate(SimpleDateFormat sdf) throws ParseException {
        return sdf.parse(value);
    }

    public TemporalAccessor getDate(DateTimeFormatter formatter) {
        return formatter.parse(value);
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(value);
    }

    public Duration getDuration() {
        return Duration.parse(value);
    }

}
