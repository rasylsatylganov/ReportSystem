package kg.home.demo.service.impl;

import kg.home.demo.entity.TbReferenceItemValue;
import kg.home.demo.repository.TbReferenceItemValueRepository;
import kg.home.demo.service.TbReferenceItemValueService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TbReferenceItemValueServiceImpl implements TbReferenceItemValueService {

    private final TbReferenceItemValueRepository tbReferenceItemValueRepository;

    public TbReferenceItemValueServiceImpl(TbReferenceItemValueRepository tbReferenceItemValueRepository) {
        this.tbReferenceItemValueRepository = tbReferenceItemValueRepository;
    }

    @Override
    public List<TbReferenceItemValue> getAllTbReferenceItemValues() {
        return tbReferenceItemValueRepository.findAll();
    }

    @Override
    public Optional<TbReferenceItemValue> getTbReferenceItemValueById(Long id) {
        return tbReferenceItemValueRepository.findById(id);
    }

    @Override
    public TbReferenceItemValue createTbReferenceItemValue(TbReferenceItemValue tbReferenceItemValue) {
        return tbReferenceItemValueRepository.save(tbReferenceItemValue);
    }

    @Override
    public TbReferenceItemValue updateTbReferenceItemValue(Long id, TbReferenceItemValue tbReferenceItemValue) {
        return tbReferenceItemValueRepository.findById(id)
                .map(existing -> {
                    existing.setReferenceItem(tbReferenceItemValue.getReferenceItem());
                    existing.setReportInReferenceItem(tbReferenceItemValue.getReportInReferenceItem());
                    existing.setValueNumber(tbReferenceItemValue.getValueNumber());
                    existing.setValueString(tbReferenceItemValue.getValueString());
                    existing.setValueDatetime(tbReferenceItemValue.getValueDatetime());
                    existing.setIndexRow(tbReferenceItemValue.getIndexRow());
                    return tbReferenceItemValueRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("TbReferenceItemValue not found with id " + id));
    }

    @Override
    public void deleteTbReferenceItemValue(Long id) {
        if (!tbReferenceItemValueRepository.existsById(id)) {
            throw new RuntimeException("TbReferenceItemValue not found with id " + id);
        }
        tbReferenceItemValueRepository.deleteById(id);
    }
}
