
package csd230.lab1.controllers;

import csd230.lab1.entities.MagazineEntity;
import csd230.lab1.exceptions.ResourceNotFoundException;
import csd230.lab1.repositories.MagazineEntityRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rest/magazines")
@Tag(name = "Magazines", description = "REST API for managing magazines")
public class MagazineRestController {

    private final MagazineEntityRepository magazineRepository;

    public MagazineRestController(MagazineEntityRepository magazineRepository) {
        this.magazineRepository = magazineRepository;
    }

    @GetMapping
    @Operation(summary = "Get all magazines")
    @ApiResponse(responseCode = "200", description = "Magazines retrieved successfully")
    public List<MagazineEntity> getAllMagazines() {
        return magazineRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get magazine by ID")
    @ApiResponse(responseCode = "200", description = "Magazine found")
    @ApiResponse(responseCode = "404", description = "Magazine not found")
    public MagazineEntity getMagazineById(@PathVariable Long id) {
        return magazineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Magazine not found with ID: " + id));
    }

    @PostMapping
    @Operation(summary = "Create a new magazine")
    @ApiResponse(responseCode = "201", description = "Magazine created successfully")
    public ResponseEntity<MagazineEntity> createMagazine(
            @RequestBody MagazineEntity magazine) {

        magazine.setId(null);
        MagazineEntity savedMagazine = magazineRepository.save(magazine);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedMagazine);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing magazine")
    @ApiResponse(responseCode = "200", description = "Magazine updated successfully")
    @ApiResponse(responseCode = "404", description = "Magazine not found")
    public MagazineEntity updateMagazine(
            @PathVariable Long id,
            @RequestBody MagazineEntity updatedMagazine) {

        MagazineEntity magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Magazine not found with ID: " + id));

        magazine.setTitle(updatedMagazine.getTitle());
        magazine.setPrice(updatedMagazine.getPrice());
        magazine.setCopies(updatedMagazine.getCopies());
        magazine.setOrderQty(updatedMagazine.getOrderQty());
        magazine.setCurrentIssue(updatedMagazine.getCurrentIssue());

        return magazineRepository.save(magazine);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a magazine")
    @ApiResponse(responseCode = "204", description = "Magazine deleted successfully")
    @ApiResponse(responseCode = "404", description = "Magazine not found")
    public ResponseEntity<Void> deleteMagazine(@PathVariable Long id) {

        MagazineEntity magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Magazine not found with ID: " + id));

        magazineRepository.delete(magazine);

        return ResponseEntity.noContent().build();
    }
}
