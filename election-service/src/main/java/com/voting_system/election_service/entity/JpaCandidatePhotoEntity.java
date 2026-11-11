package com.voting_system.election_service.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fotografía de un candidato. Vive en su propia tabla para que listar candidatos
 * o consultar el detalle de una elección no cargue los bytes de las imágenes.
 */
@Entity
@Table (name = "candidate_photos")
@Getter
@Setter
@NoArgsConstructor
public class JpaCandidatePhotoEntity {
    @Id
    @Column (name = "candidate_id")
    private UUID candidateId;

    @MapsId
    @OneToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "candidate_id")
    private JpaCandidateEntity candidate;

    @Column (name = "content_type", nullable = false, length = 20)
    private String contentType;

    @Column (name = "size_bytes", nullable = false)
    private int sizeBytes;

    // SHA-256 en hexadecimal; se usa como ETag al servir la imagen.
    @Column (nullable = false, length = 64)
    private String sha256;

    // En PostgreSQL, Hibernate mapea byte[] a bytea.
    @Column (nullable = false)
    private byte[] data;

    @UpdateTimestamp
    @Column (nullable = false)
    private Instant updatedAt;
}
