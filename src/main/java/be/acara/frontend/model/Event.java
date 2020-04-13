package be.acara.frontend.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    private Long id;

    @Future(message = "Date must be in the future")
    @NotNull(message = "Field cannot be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime eventDate;

    @NotBlank(message = "NotBlank.event.name")
    private String name;

    @NotBlank(message = "NotBlank.event.description")
    private String description;

    private byte[] image;

    @NotBlank(message = "NotBlank.event.location")
    private String location;

    @NotBlank(message = "NotBlank.event.category")
    private String category;

    @NotNull(message = "Minimum price is 1 euro")
    @Min(value = 1, message = "Minimum price is 1 euro")
    private BigDecimal price;
}