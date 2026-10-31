
package csd230.lab1.controllers;

import csd230.lab1.entities.TicketEntity;
import csd230.lab1.exceptions.ResourceNotFoundException;
import csd230.lab1.repositories.TicketEntityRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rest/tickets")
@Tag(name = "Tickets", description = "REST API for managing tickets")
public class TicketRestController {

    private final TicketEntityRepository ticketRepository;

    public TicketRestController(TicketEntityRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    @Operation(summary = "Get all tickets")
    @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully")
    public List<TicketEntity> getAllTickets() {
        return ticketRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID")
    @ApiResponse(responseCode = "200", description = "Ticket found")
    @ApiResponse(responseCode = "404", description = "Ticket not found")
    public TicketEntity getTicketById(@PathVariable Long id) {

        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with ID: " + id));
    }

    @PostMapping
    @Operation(summary = "Create a new ticket")
    @ApiResponse(responseCode = "201", description = "Ticket created successfully")
    public ResponseEntity<TicketEntity> createTicket(
            @RequestBody TicketEntity ticket) {

        ticket.setId(null);
        TicketEntity savedTicket = ticketRepository.save(ticket);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTicket);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing ticket")
    @ApiResponse(responseCode = "200", description = "Ticket updated successfully")
    @ApiResponse(responseCode = "404", description = "Ticket not found")
    public TicketEntity updateTicket(
            @PathVariable Long id,
            @RequestBody TicketEntity updatedTicket) {

        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with ID: " + id));

        ticket.setDescription(updatedTicket.getDescription());
        ticket.setPrice(updatedTicket.getPrice());

        return ticketRepository.save(ticket);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a ticket")
    @ApiResponse(responseCode = "204", description = "Ticket deleted successfully")
    @ApiResponse(responseCode = "404", description = "Ticket not found")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {

        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with ID: " + id));

        ticketRepository.delete(ticket);

        return ResponseEntity.noContent().build();
    }
}
