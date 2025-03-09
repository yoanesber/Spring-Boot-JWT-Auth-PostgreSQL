package com.yoanesber.spring.security.jwt_auth_postgresql.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoanesber.spring.security.jwt_auth_postgresql.dto.NetflixShowsDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.CustomHttpResponse;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.NetflixShowsService;

@RestController
@RequestMapping("/api/v1/netflix-shows")
public class NetflixShowsController {

    private final NetflixShowsService netflixShowsService;

    public NetflixShowsController(NetflixShowsService netflixShowsService) {
        this.netflixShowsService = netflixShowsService;
    }

    @PostMapping
    public ResponseEntity<Object> createNetflixShows(@RequestBody NetflixShowsDTO netflixShowsDTO) {
        try {
            // Check if the input is null
            if (netflixShowsDTO == null) {
                return ResponseEntity.badRequest().body(new CustomHttpResponse(HttpStatus.BAD_REQUEST.value(), 
                    "NetflixShowsDTO must not be null", null));
            }

            // Create Netflik show & Return the response
            return ResponseEntity.created(null).body(new CustomHttpResponse(HttpStatus.CREATED.value(), 
                "NetflixShows created successfully", netflixShowsService.createNetflixShows(netflixShowsDTO)));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllNetflixShows() {
        try {
            // Get all NetflixShows
            List<NetflixShowsDTO> netflixShows = netflixShowsService.getAllNetflixShows();

            // Check if the list is empty
            if (netflixShows == null || netflixShows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomHttpResponse(HttpStatus.NOT_FOUND.value(), 
                    "No NetflixShows found", null));
            }

            // Return the response
            return ResponseEntity.ok(new CustomHttpResponse(HttpStatus.OK.value(), 
                "NetflixShows retrieved successfully", netflixShows));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getNetflixShowsById(@PathVariable Long id) {
        try {
            // Check if the ID is null
            if (id == null) {
                return ResponseEntity.badRequest().body(new CustomHttpResponse(HttpStatus.BAD_REQUEST.value(), 
                    "ID must not be null", null));
            }

            // Get the NetflixShows by ID
            NetflixShowsDTO netflixShows = netflixShowsService.getNetflixShowsById(id);

            // Check if the NetflixShows is null
            if (netflixShows == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomHttpResponse(HttpStatus.NOT_FOUND.value(), 
                    "NetflixShows not found", null));
            }

            // Return the response
            return ResponseEntity.ok(new CustomHttpResponse(HttpStatus.OK.value(), 
                "NetflixShows retrieved successfully", netflixShows));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateNetflixShows(@PathVariable Long id, @RequestBody NetflixShowsDTO netflixShowsDTO) {
        try {
            // Check if the ID and NetflixShowsDTO is null
            if (id == null) {
                return ResponseEntity.badRequest().body(new CustomHttpResponse(HttpStatus.BAD_REQUEST.value(), 
                    "ID must not be null", null));
            }

            if (netflixShowsDTO == null) {
                return ResponseEntity.badRequest().body(new CustomHttpResponse(HttpStatus.BAD_REQUEST.value(), 
                    "NetflixShowsDTO must not be null", null));
            }

            // Update the NetflixShows
            NetflixShowsDTO netflixShows = netflixShowsService.updateNetflixShows(id, netflixShowsDTO);

            // Check if the NetflixShows is null
            if (netflixShows == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomHttpResponse(HttpStatus.NOT_FOUND.value(), 
                    "NetflixShows not found", null));
            }

            // Return the response
            return ResponseEntity.ok(new CustomHttpResponse(HttpStatus.OK.value(), 
                "NetflixShows updated successfully", netflixShows));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteNetflixShows(@PathVariable Long id) {
        try {
            // Check if the ID is null
            if (id == null) {
                return ResponseEntity.badRequest().body(new CustomHttpResponse(HttpStatus.BAD_REQUEST.value(), 
                    "ID must not be null", null));
            }

            // Delete the NetflixShows
            if (!netflixShowsService.deleteNetflixShows(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomHttpResponse(HttpStatus.NOT_FOUND.value(), 
                    "NetflixShows not found", null));
            }

            // Return the response
            return ResponseEntity.ok(new CustomHttpResponse(HttpStatus.OK.value(), 
                "NetflixShows deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                e.getMessage(), null));
        }
    }
    
}
