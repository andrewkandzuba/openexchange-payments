package org.openexchange.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Currency implements Serializable {
    private static final long serialVersionUID = -4875874173742367410L;
    @Id
    @Column(length = 3, unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private String description;

    public Currency() {
    }

    public Currency(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
