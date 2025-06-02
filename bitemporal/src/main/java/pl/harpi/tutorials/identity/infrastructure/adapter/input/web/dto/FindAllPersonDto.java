package pl.harpi.tutorials.identity.infrastructure.adapter.input.web.dto;

public record FindAllPersonDto(String logicalId, String pesel, String firstName, String lastName) {
}
