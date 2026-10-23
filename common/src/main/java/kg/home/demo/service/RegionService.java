package kg.home.demo.service;

import kg.home.demo.entity.Region;

import java.util.List;
import java.util.Optional;

public interface RegionService {

    List<Region> getAllRegions();

    Optional<Region> getRegionById(Integer id);

    Region saveRegion(Region region);

    Region updateRegion(Integer id, Region region);

    void deleteRegion(Integer id);
}