package com.db.account.service;

import com.db.account.mapper.AccountMapper;
import com.db.account.model.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.db.account.entity.BranchEntity;
import com.db.account.model.BranchDto;
import com.db.account.repository.BranchRepository;
import com.db.account.mapper.BranchMapper;
import com.db.account.mapper.CycleAvoidingMappingContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class BranchService {

    private final BranchRepository branchRepository;

    private final BranchMapper branchMapper;

    private final AccountMapper accountMapper;

    /***
     *
     * @param branchDto
     * @return
     * @throws Exception
     */
    public BranchDto createBranch(BranchDto branchDto) throws Exception {

        BranchEntity newBranch = branchRepository.save(branchMapper.toEntity(branchDto, new CycleAvoidingMappingContext()));
        return branchMapper.toDto(newBranch, new CycleAvoidingMappingContext());
    }


    /***
     *
     * @param branchCode
     * @return
     * @throws Exception
     */
    public BranchDto findBranchByBranchCode(long branchCode) throws Exception {
        Optional<BranchEntity> branch = branchRepository.findById(branchCode);

        if(branch.isEmpty() || branchRepository.isActiveFalse(branchCode)){
            throw new Exception("Branch with the given Id not found");
        }
        return branchMapper.toDto(branch.get(), new CycleAvoidingMappingContext());
    }

    /***
     *
     * @param branchCode
     * @return
     * @throws Exception
     */
    public List<AccountDto> findAllAccountsForABranch(long branchCode) throws Exception {
        Optional<BranchEntity> branch = branchRepository.findById(branchCode);

        return branch
                .map(branchEntity -> branchEntity.getAccountEntities()
                        .stream()
                                .map(accountEntity -> accountMapper.toDto(accountEntity, new CycleAvoidingMappingContext()))
                                .toList())
                .orElseThrow(() -> new Exception("Branch with the given Id not found"));
    }


    /***
     *
     * @return
     * @throws Exception
     */
    public List<BranchDto> findAllBranches() throws Exception {

        List<BranchEntity> activeBranches = branchRepository.findByIsActiveTrue();

        return activeBranches.stream()
                .map(branch -> branchMapper.toDto(branch, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    public BranchDto updatePartiallyFromDto(long branchCode, BranchDto branchDto) throws Exception {

        BranchEntity branchEntity = branchRepository.findById(branchCode)
                .orElseThrow(() -> new Exception("Branch with the given Id not found"));

        branchMapper.updatePartiallyFromDto(branchDto, branchEntity);
        return branchMapper.toDto(branchEntity, new CycleAvoidingMappingContext());
    }

    /***
     *
     * @param branchCode
     * @throws Exception
     */
    public void deleteBranch(long branchCode) throws Exception{

        BranchEntity branchToBeDeleted = branchRepository.findById(branchCode)
                .orElseThrow(() -> new Exception("Branch with the given Id not found"));

        branchToBeDeleted.setIsActive(true);
        branchRepository.save(branchToBeDeleted);
    }
}
