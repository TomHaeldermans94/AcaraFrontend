package be.acara.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateOrderModel {
    private EventModel eventModel;
    private int amountOfTickets;
}
