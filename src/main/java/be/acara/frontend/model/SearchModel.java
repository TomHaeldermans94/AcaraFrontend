package be.acara.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchModel {
    private String name;
    private String location;
    private String minPrice;
    private String maxPrice;
    private String category;
    private String startDate;
    private String endDate;
}
