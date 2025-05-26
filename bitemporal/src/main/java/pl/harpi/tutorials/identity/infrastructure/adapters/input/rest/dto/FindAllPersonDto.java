package pl.harpi.tutorials.identity.infrastructure.adapters.input.rest.dto;

public record FindAllPersonDto(String logicalId, String pesel, String firstName, String lastName) {
}
