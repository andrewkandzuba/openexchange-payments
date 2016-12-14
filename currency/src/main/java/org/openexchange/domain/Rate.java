package org.openexchange.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Access(AccessType.FIELD)
public class Rate implements Serializable {
    private static final long serialVersionUID = 5160208533569040830L;

    @EmbeddedId
    private RatePK id;
    @Column(precision = 10, scale = 6, nullable = false)
    private BigDecimal quote;
    @Column(nullable = false)
    @Type(type="timestamp")
    private Date timestamp;

    public Rate() {
    }

    public Rate(Currency source, Currency target, BigDecimal quote, Date timestamp) {
        this.id = new RatePK(source, target);
        this.quote = quote;
        this.timestamp = timestamp;
    }

    public Currency getSource(){
        return id.getSource();
    }
    public Currency getTarget(){
        return id.getTarget();
    }
    public BigDecimal getQuote() {
        return quote;
    }
    public Date getTimestamp() {
        return timestamp;
    }

    @Embeddable
    @Access(AccessType.FIELD)
    public static class RatePK implements Serializable {
        private static final long serialVersionUID = 6663463117714621191L;

        @OneToOne
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(referencedColumnName = "code")
        private Currency source;
        @OneToOne
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(referencedColumnName = "code")
        private Currency target;

        public RatePK() {
        }

        public RatePK(Currency source, Currency target) {
            this.source = source;
            this.target = target;
        }

        public boolean equals(Object object) {
            if (object instanceof RatePK) {
                RatePK pk = (RatePK) object;
                return source.equals(pk.source) && target.equals(pk.target);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return source.hashCode() + target.hashCode();
        }

        Currency getSource() {
            return source;
        }
        Currency getTarget() {
            return target;
        }
    }
}
