package Ex2;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

enum TipChitara {
    ELECTRICA, ACUSTICA, CLASICA
}

enum TipTobe {
    ELECTRONICE, ACUSTICE
}

abstract class InstrumentMuzical {
    String producator;
    double pret;

    public InstrumentMuzical(){}
    public InstrumentMuzical(String producator, double pret) {
        this.producator = producator;
        this.pret = pret;
    }

    public String getProducator()
    {
        return producator;
    }

    public double getPret()
    {
        return pret;
    }

    @Override
    public String toString()
    {
        return "InstrumentMuzical{" +
                "producator='" + producator + '\'' +
                ", pret=" + pret +
                '}';
    }
}

class Chitara extends InstrumentMuzical {
    TipChitara tip_chitara;
    int nr_corzi;

    public Chitara() {}

    public Chitara(String producator, double pret, TipChitara tip_chitara, int nr_corzi) {
        super(producator, pret);
        this.tip_chitara = tip_chitara;
        this.nr_corzi = nr_corzi;
    }
    public TipChitara getTip_chitara() {
        return tip_chitara;
    }

    public int getNr_corzi() {
        return nr_corzi;
    }

    @Override
    public String toString() {
        return "Chitara{" +
                "producator='" + producator + '\'' +
                ", pret=" + pret +
                ", tip_chitara=" + tip_chitara +
                ", nr_corzi=" + nr_corzi +
                '}';
    }
}

class SetTobe extends InstrumentMuzical {
    TipTobe tip_tobe;
    int nr_tobe;
    int nr_cinele;

    public SetTobe(){}
    public SetTobe(String producator, double pret, TipTobe tip_tobe, int nr_tobe, int nr_cinele) {
        super(producator, pret);
        this.tip_tobe = tip_tobe;
        this.nr_tobe = nr_tobe;
        this.nr_cinele = nr_cinele;
    }

    public TipTobe getTip_tobe()
    {
        return tip_tobe;
    }

    public int getNr_tobe()
    {
        return nr_tobe;
    }

    public int getNr_cinele()
    {
        return nr_cinele;
    }

    @Override
    public String toString() {
        return "SetTobe{" +
                "producator='" + producator + '\'' +
                ", pret=" + pret +
                ", tip_tobe=" + tip_tobe +
                ", nr_tobe=" + nr_tobe +
                ", nr_cinele=" + nr_cinele +
                '}';
    }
}

public class MainApp {
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    abstract static class Mixin {}

    public static void main(String[] args) {
        Set<InstrumentMuzical> instrumenteSet = new HashSet<>();
        instrumenteSet.add(new Chitara("Fender", 2500, TipChitara.ELECTRICA, 6));
        instrumenteSet.add(new Chitara("Yamaha", 1200, TipChitara.ACUSTICA, 12));
        instrumenteSet.add(new Chitara("Ibanez", 3500, TipChitara.CLASICA, 6));
        instrumenteSet.add(new SetTobe("Roland", 1800, TipTobe.ELECTRONICE, 5, 3));
        instrumenteSet.add(new SetTobe("Pearl", 3000, TipTobe.ACUSTICE, 3, 2));
        instrumenteSet.add(new SetTobe("Mapex", 2500, TipTobe.ACUSTICE, 4, 1));

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
            mapper.writeValue(new File("src/main/resources/instrumente.json"), instrumenteSet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<InstrumentMuzical> instrumenteCitite = null;
        try {
            instrumenteCitite = mapper.readValue(new File("src/main/resources/instrumente.json"), new TypeReference<Set<InstrumentMuzical>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Implementarea utilizată pentru interfața Set: " + instrumenteCitite.getClass());

        Chitara chitaraDuplicata = new Chitara("Yamaha", 1200, TipChitara.ACUSTICA, 12);
        if (!instrumenteSet.add(chitaraDuplicata)) {
            System.out.println("Instrument duplicat: " + chitaraDuplicata.producator);
        }

        instrumenteSet.removeIf(instrument -> instrument.pret > 3000);

        instrumenteCitite.stream()
                .filter(instrument -> instrument instanceof Chitara)
                .forEach(System.out::println);

        instrumenteCitite.stream()
                .filter(instrument -> instrument instanceof SetTobe)
                .forEach(System.out::println);

        instrumenteSet.stream()
                .filter(instrument -> instrument instanceof Chitara)
                .max((i1, i2) -> ((Chitara) i1).nr_corzi - ((Chitara) i2).nr_corzi)
                .ifPresent(System.out::println);

        instrumenteSet.stream()
                .filter(instrument -> instrument instanceof SetTobe)
                .filter(instrument -> ((SetTobe) instrument).tip_tobe == TipTobe.ACUSTICE)
                .sorted((i1, i2) -> ((SetTobe) i1).nr_tobe - ((SetTobe) i2).nr_tobe)
                .forEach(System.out::println);
    }
}
