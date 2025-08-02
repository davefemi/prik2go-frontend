package nl.davefemi.prik2go.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object voor klantgegevens van een vestiging
 * Alle klantnummers worden in een lijst opgenomen en het aantal 
 * klanten opgeslagen.
 */
@Getter
@Setter
public class KlantenDTO {
        private List<Integer> klantNummers = new ArrayList<>();
        private int aantalKlanten;
}
