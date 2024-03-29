package br.com.jacto.trevo.service.product;

import br.com.jacto.trevo.controller.product.dto.ProductCultureDto;
import br.com.jacto.trevo.controller.product.form.ProductCultureDeleteForm;
import br.com.jacto.trevo.controller.product.form.ProductCultureForm;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.repository.CultureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CultureService {

    @Autowired
    private CultureRepository cultureRepository;

    public Culture create(Culture culture) {
        return cultureRepository.save(culture);
    }

    public Optional<Culture> getId(UUID id) {
        return cultureRepository.findById(id);
    }

    public Optional<ProductCultureDto> update(ProductCultureForm culture) {

        Optional<Culture> findCulture = cultureRepository.findById(culture.getCultureId());

        if (findCulture.isEmpty()) {
            return Optional.empty();
        }

        if (!findCulture.get().getProduct().getProductId().equals(culture.getProductId())) {
            return Optional.empty();
        }

        findCulture.get().setCultureName(culture.getCultureName());

        return Optional.of(cultureRepository.save(findCulture.get())).map(ProductCultureDto::new);
    }

    public Boolean delete(ProductCultureDeleteForm culture) {
        Optional<Culture> findCulture = cultureRepository.findById(culture.getCultureId());

        if (findCulture.isEmpty()) {
            return false;
        }

        if (!findCulture.get().getProduct().getProductId().equals(culture.getProductId())) {
            return false;
        }

        cultureRepository.deleteById(culture.getCultureId());
        return true;
    }
}
