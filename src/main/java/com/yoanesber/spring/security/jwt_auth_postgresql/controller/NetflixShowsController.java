package com.yoanesber.spring.security.jwt_auth_postgresql.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoanesber.spring.security.jwt_auth_postgresql.dto.HttpResponseDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.dto.NetflixShowsDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.NetflixShowsService;
import com.yoanesber.spring.security.jwt_auth_postgresql.util.ResponseUtil;

/**
 * NetflixShowsController is a REST controller that handles HTTP requests related to Netflix shows.
 * It provides endpoints for creating, retrieving, updating, and deleting Netflix shows.
 * The controller uses the NetflixShowsService to perform the actual operations.
 */

@RestController
@RequestMapping("/api/v1/netflix-shows")
public class NetflixShowsController {

    private final NetflixShowsService netflixShowsService;

    private static final String INVALID_REQUEST = "Invalid Request";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String RECORD_NOT_FOUND = "Record not found";
    private static final String RECORD_RETRIEVED_SUCCESSFULLY = "Record retrieved successfully";
    private static final String RECORD_CREATED_SUCCESSFULLY = "Record created successfully";
    private static final String RECORD_UPDATED_SUCCESSFULLY = "Record updated successfully";
    private static final String RECORD_DELETED_SUCCESSFULLY = "Record deleted successfully";

    public NetflixShowsController(NetflixShowsService netflixShowsService) {
        this.netflixShowsService = netflixShowsService;
    }

    @PostMapping
    public ResponseEntity<HttpResponseDTO> createNetflixShows(@RequestBody NetflixShowsDTO netflixShowsRequest,
        HttpServletRequest request) {
        // Validate the request body
        if (netflixShowsRequest == null || 
            netflixShowsRequest.getTitle() == null || 
            netflixShowsRequest.getTitle().isEmpty()) {
            // Return a bad request response if the request body is invalid
            return ResponseUtil.buildBadRequestResponse(request, 
                INVALID_REQUEST,
                "NetflixShowsRequest must not be null or empty", 
                null);
        }

        try {
            // Create Netflik show & Return the response
            return ResponseUtil.buildOkResponse(request, 
                RECORD_CREATED_SUCCESSFULLY,
                netflixShowsService.createNetflixShows(netflixShowsRequest));
        } catch (Exception e) {
            // Return an internal server error response if an exception occurs
            return ResponseUtil.buildInternalServerErrorResponse(request, 
                INTERNAL_SERVER_ERROR,
                "An error occurred while creating NetflixShows: " + e.getMessage(), 
                null);
        }
    }

    @GetMapping
    public ResponseEntity<HttpResponseDTO> getAllNetflixShows(HttpServletRequest request) {
        try {
            // Get all NetflixShows
            List<NetflixShowsDTO> netflixShows = netflixShowsService.getAllNetflixShows();

            // Check if the list is empty
            if (netflixShows == null || netflixShows.isEmpty()) {
                // Return a not found response if no NetflixShows are found
                return ResponseUtil.buildNotFoundResponse(request, 
                    RECORD_NOT_FOUND, 
                    "No NetflixShows found in the database", 
                    null);
            }

            // Return the response with the list of NetflixShows
            return ResponseUtil.buildOkResponse(request, 
                RECORD_RETRIEVED_SUCCESSFULLY,
                netflixShows);
        } catch (Exception e) {
            // Return an internal server error response if an exception occurs
            return ResponseUtil.buildInternalServerErrorResponse(request, 
                INTERNAL_SERVER_ERROR, 
                "An error occurred while retrieving NetflixShows: " + e.getMessage(), 
                null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponseDTO> getNetflixShowsById(@PathVariable Long id,
        HttpServletRequest request) {
        // Validate the ID
        if (id == null) {
            // Return a bad request response if the ID is null
            return ResponseUtil.buildBadRequestResponse(request, 
                INVALID_REQUEST,
                "ID must not be null", 
                null);
        }

        try {
            // Get the NetflixShows by ID
            NetflixShowsDTO netflixShows = netflixShowsService.getNetflixShowsById(id);

            // Check if the NetflixShows is null
            if (netflixShows == null) {
                // Return a not found response if the NetflixShows is not found
                return ResponseUtil.buildNotFoundResponse(request, 
                    RECORD_NOT_FOUND, 
                    "NetflixShows not found with ID: " + id, 
                    null);
            }

            // Return ok response with the NetflixShows
            return ResponseUtil.buildOkResponse(request, 
                RECORD_RETRIEVED_SUCCESSFULLY,
                netflixShows);
        } catch (Exception e) {
            // Return an internal server error response if an exception occurs
            return ResponseUtil.buildInternalServerErrorResponse(request, 
                INTERNAL_SERVER_ERROR, 
                "An error occurred while retrieving NetflixShows: " + e.getMessage(), 
                null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponseDTO> updateNetflixShows(@PathVariable Long id, 
        @RequestBody NetflixShowsDTO netflixShowsRequest, HttpServletRequest request) {
        // Validate the ID
        if (id == null) {
            // Return a bad request response if the ID is null
            return ResponseUtil.buildBadRequestResponse(request, 
                INVALID_REQUEST,
                "ID must not be null", 
                null);
        }

        // Validate the request body
        if (netflixShowsRequest == null ||
            netflixShowsRequest.getTitle() == null ||
            netflixShowsRequest.getTitle().isEmpty()) {
            // Return a bad request response if the request body is invalid
            return ResponseUtil.buildBadRequestResponse(request, 
                INVALID_REQUEST,
                "NetflixShowsRequest must not be null or empty", 
                null);
        }

        try {
            // Update the NetflixShows
            NetflixShowsDTO updatedNetflixShows = netflixShowsService
                .updateNetflixShows(id, netflixShowsRequest);

            // Check if the NetflixShows is null
            if (updatedNetflixShows == null) {
                // Return a not found response if the NetflixShows is not found
                return ResponseUtil.buildNotFoundResponse(request, 
                    RECORD_NOT_FOUND, 
                    "NetflixShows not found with ID: " + id, 
                    null);
            }

            // Return ok response with the updated NetflixShows
            return ResponseUtil.buildOkResponse(request, 
                RECORD_UPDATED_SUCCESSFULLY,
                updatedNetflixShows);
        } catch (Exception e) {
            // Return an internal server error response if an exception occurs
            return ResponseUtil.buildInternalServerErrorResponse(request, 
                INTERNAL_SERVER_ERROR, 
                "An error occurred while updating NetflixShows: " + e.getMessage(), 
                null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponseDTO> deleteNetflixShows(@PathVariable Long id,
        HttpServletRequest request) {
        // Validate the ID
        if (id == null) {
            // Return a bad request response if the ID is null
            return ResponseUtil.buildBadRequestResponse(request, 
                INVALID_REQUEST,
                "ID must not be null", 
                null);
        }

        try {
            // Delete the NetflixShows
            if (!netflixShowsService.deleteNetflixShows(id)) {
                // Return a not found response if the NetflixShows is not found
                return ResponseUtil.buildNotFoundResponse(request, 
                    RECORD_NOT_FOUND, 
                    "NetflixShows not found with ID: " + id, 
                    null);
            }

            // Return ok response if the NetflixShows is deleted successfully
            return ResponseUtil.buildOkResponse(request, 
                RECORD_DELETED_SUCCESSFULLY,
                null);
        } catch (Exception e) {
            // Return an internal server error response if an exception occurs
            return ResponseUtil.buildInternalServerErrorResponse(request, 
                INTERNAL_SERVER_ERROR, 
                "An error occurred while deleting NetflixShows: " + e.getMessage(), 
                null);
        }
    }
    
}
