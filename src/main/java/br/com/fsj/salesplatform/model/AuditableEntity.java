package br.com.fsj.salesplatform.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

/**
 * Classe base abstrata que fornece campos de auditoria para todas as entidades.
 * 
 * <p>
 * Esta classe utiliza {@link MappedSuperclass} para que seus campos sejam
 * herdados pelas entidades filhas sem criar uma tabela separada.
 * </p>
 * 
 * <p>
 * <strong>Campos de Auditoria:</strong>
 * </p>
 * <ul>
 * <li>{@code createdAt} - Data/hora de criação do registro (imutável)</li>
 * <li>{@code createdBy} - Identificador do usuário que criou o registro</li>
 * <li>{@code updatedAt} - Data/hora da última atualização</li>
 * <li>{@code updatedBy} - Identificador do usuário que fez a última
 * atualização</li>
 * </ul>
 * 
 * <p>
 * <strong>Comportamento Automático:</strong>
 * </p>
 * <ul>
 * <li>{@code createdAt} e {@code updatedAt} são preenchidos automaticamente via
 * {@link PrePersist} e {@link PreUpdate}</li>
 * <li>{@code createdBy} e {@code updatedBy} devem ser definidos manualmente ou
 * via interceptor/listener</li>
 * </ul>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@MappedSuperclass
public abstract class AuditableEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    /**
     * Callback executado antes da persistência inicial da entidade.
     * Define automaticamente {@code createdAt} e {@code updatedAt} com o timestamp
     * atual.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Callback executado antes de qualquer atualização da entidade.
     * Atualiza automaticamente {@code updatedAt} com o timestamp atual.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters e Setters

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
