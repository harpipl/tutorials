package pl.harpi.tutorials.identity.infrastructure.adapters.input.rest.dto;

public record CreatePersonDto(String pesel, String firstName, String lastName) {
}
