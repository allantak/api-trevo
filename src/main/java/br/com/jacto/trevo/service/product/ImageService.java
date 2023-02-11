package br.com.jacto.trevo.service.product;

import br.com.jacto.trevo.controller.image.dto.ImageDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageDto;
import br.com.jacto.trevo.controller.image.form.ImageDeleteForm;
import br.com.jacto.trevo.controller.image.form.ImageUpdateForm;
import br.com.jacto.trevo.controller.image.form.ProductImageForm;
import br.com.jacto.trevo.model.product.Image;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.ImageRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ProductRepository productRepository;

    public Optional<ImageDto> getImage(UUID id){
        Optional<Image> img =  imageRepository.findById(id);
        return img.map(ImageDto::new);
    }

    public Optional<ProductImageDto> getImageProduct(UUID id){
        Optional<Product> product =  productRepository.findById(id);
        return product.map(ProductImageDto::new);
    }

    public Image upload(ProductImageForm image) throws IOException {

        Optional<Product> product = productRepository.findById(image.getProductId());
        if (product.isEmpty()) {
            return null;
        }

        byte[] imageBytes = image.getImage().getBytes();
        Image uploadImage = new Image(imageBytes, product.get());

        imageRepository.save(uploadImage);

        return uploadImage;
    }

    public Optional<ImageDto> updateImage(ImageUpdateForm img) throws IOException {
        Optional<Image> findImage = imageRepository.findById(img.getImageId());
        if (findImage.isEmpty()) {
            return Optional.empty();
        }

        if (!findImage.get().getProduct().getProductId().equals(img.getProductId())){
            return Optional.empty();
        }

        byte[] imageBytes = img.getImg().getBytes();
        findImage.get().setImg(imageBytes);

        Image update = imageRepository.save(findImage.get());
        return Optional.of(new ImageDto(update));
    }

    public Optional<Image> deleteImage(ImageDeleteForm img){
        Optional<Image> findImage = imageRepository.findById(img.getImageId());
        if (findImage.isEmpty()) {
            return Optional.empty();
        }

        if (!findImage.get().getProduct().getProductId().equals(img.getProductId())){
            return Optional.empty();
        }

        imageRepository.deleteById(findImage.get().getImageId());
        return findImage;
    }





}
