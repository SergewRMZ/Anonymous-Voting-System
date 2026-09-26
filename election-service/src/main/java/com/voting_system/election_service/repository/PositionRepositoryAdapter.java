package com.voting_system.election_service.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.entity.JpaPositionEntity;
import com.voting_system.election_service.mappers.PositionMapper;
import com.voting_system.election_service.repository.interfaces.IPositionRepository;
import com.voting_system.election_service.repository.jpa.JpaPositionRepository;

import lombok.RequiredArgsConstructor;


@Repository 
@RequiredArgsConstructor 
public class PositionRepositoryAdapter implements IPositionRepository {
    private final JpaPositionRepository jpaPositionRepository;
    private final PositionMapper positionMapper;

    @Override 
    public PositionModel save(PositionModel positionModel) {
        JpaPositionEntity entity = jpaPositionRepository.save(positionMapper.toEntity(positionModel));
        return positionMapper.toModel(entity);
    }

    @Override 
    public List<PositionModel> getPositions() {
        List<JpaPositionEntity> list = jpaPositionRepository.findAll();
        return list.stream()
            .map(positionMapper::toModel)
            .toList();
    }
} 
