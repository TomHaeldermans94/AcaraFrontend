package be.acara.frontend.util;

import be.acara.frontend.model.EventModelList;
import org.springframework.ui.Model;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface PaginationUtil {
    /**
     * An utility method to add page numbers to a model
     * @param events an eventmodellist, containing the events
     * @param model the model of the request to add the numbers to
     * @param attribute the attribute to reference to get the page numbers
     */
    static void addPageNumbers(EventModelList events, Model model, String attribute) {
        if (events.getTotalPages() == 1) {
            return;
        }
        model.addAttribute(attribute, IntStream.rangeClosed(1, events.getTotalPages())
                .boxed()
                .collect(Collectors.toList()));
    }
}
