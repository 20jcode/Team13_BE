package dbdr.domain.guardian.entity;

import dbdr.domain.recipient.entity.Recipient;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import dbdr.domain.core.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "guardians")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE guardians SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Guardian extends BaseEntity {
    @Column(unique = true)
    private String loginId;

    @Column(nullable = false)
    private String loginPassword;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "010\\d{8}")
    private String phone;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = true)
    private String lineUserId;

    @Column(nullable = true)
    private LocalTime alertTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id",nullable = true)
    private Recipient recipient;

    @Builder
    protected Guardian(String loginId, String loginPassword, String phone, String name,
        String lineUserId,
        LocalTime alertTime, Recipient recipient) {
        this.loginId = loginId;
        this.loginPassword = loginPassword;
        this.phone = phone;
        this.name = name;
        this.lineUserId = lineUserId;
        if(alertTime == null) {
            alertTime = LocalTime.of(9, 0);
        }
        this.alertTime = alertTime;
        this.recipient = recipient;
    }

    public void updateGuardian(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    public void updateLineUserId(String lineUserId) {
        this.lineUserId = lineUserId;
    }

    public void updateAlertTime(LocalTime alertTime) {
        this.alertTime = alertTime;
    }
}
