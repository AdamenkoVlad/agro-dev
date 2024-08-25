package com.abi.agro_back.service.impl;

import com.abi.agro_back.collection.Exhibition;
import com.abi.agro_back.collection.Photo;
import com.abi.agro_back.config.StorageService;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.repository.ExhibitionRepository;
import com.abi.agro_back.service.ExhibitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
public class ExhibitionServiceImpl implements ExhibitionService {

    @Autowired
    private ExhibitionRepository exhibitionRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public Exhibition createExhibition(Exhibition exhibition) {

        return exhibitionRepository.save(exhibition);
    }

    @Override
    public Exhibition getExhibitionById(String exhibitionId) {

        return exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Exhibition is not exists with given id : " + exhibitionId));
    }

    @Override
    public List<Exhibition> getAllExhibitions() {

        return exhibitionRepository.findAll();
    }

    @Override
    public Exhibition updateExhibition(String exhibitionId, MultipartFile image, List<MultipartFile> photos, Exhibition updatedExhibition) throws IOException {

        Exhibition exhibition = exhibitionRepository.findById(exhibitionId).orElseThrow(
                () -> new ResourceNotFoundException("Exhibition is not exists with given id: " + exhibitionId)
        );
        updatedExhibition.setId(exhibition.getId());

        if (photos != null) {
            for (MultipartFile f : photos) {
                String key = f.getOriginalFilename() + "" + System.currentTimeMillis();
                URL url = storageService.uploadPhoto(f, key);
                Photo photo = new Photo(key, url);
                updatedExhibition.getGalleryPhotos().add(photo);
            }
        }

        if (image != null) {
            String imageKey = System.currentTimeMillis()+ "" + image.getOriginalFilename();
            URL imageUrl = storageService.uploadPhoto(image, imageKey);
            Photo imagePhoto = new Photo(imageKey, imageUrl);
            if (exhibition.getImage() != null) {
                storageService.deletePhoto(exhibition.getImage().getKey());
            }
            updatedExhibition.setImage(imagePhoto);
        }

        return exhibitionRepository.save(updatedExhibition);
    }

    @Override
    public void deleteExhibitionById(String exhibitionId) {

        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exhibition is not exists with given id : " + exhibitionId));
        if (exhibition.getImage() != null) {
            storageService.deletePhoto(exhibition.getImage().getKey());
        }
        if (exhibition.getGalleryPhotos() != null) {
            for (Photo photo : exhibition.getGalleryPhotos()){
                storageService.deletePhoto(photo.getKey());
            }
        }
        exhibitionRepository.deleteById(exhibitionId);
    }

    @Override
    public List<Exhibition> getExhibitionsByDate(Date start, Date end) {
        start.setHours(0);
        end.setHours(24);
        return exhibitionRepository.findExhibitionsByStartDateIsBetweenOrEndDateIsBetweenOrStartDateBeforeAndEndDateAfterOrderByStartDate(start, end, start, end, start, end);
    }

    @Override
    public Page<Exhibition> getExhibitionsArchive(Pageable pageable) {
        Date now = new Date();
        now.setHours(0);
        return exhibitionRepository.findExhibitionsByEndDateBeforeOrderByEndDateDesc(now, pageable);
    }

    @Override
    public Page<Exhibition> findAllByPage(Pageable pageable) {
        Date now = new Date();
        now.setHours(0);
        return exhibitionRepository.findExhibitionsByEndDateAfterOrEndDateIsNullOrderByEndDateDesc(now, pageable);
    }

    @Override
    public List<Exhibition> getExhibitionsByKeySearch(String key, String oblast) {
        if (!oblast.isEmpty()) {
            return exhibitionRepository.findExhibitionsByKeyWordsIsContainingIgnoreCaseAndLocationIsIgnoreCase(key, oblast);
        } else {
            return exhibitionRepository.findExhibitionsByKeyWordsIsContainingIgnoreCase(key);
        }
    }
}
