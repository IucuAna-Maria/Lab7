package Ex1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

record Carte(String titlu, String autor, int anAparitie) {}

public class MainApp
{
    private static final Scanner scanner = new Scanner(System.in);

    private static Map<Integer, Carte> citesteCartiDinJSON(String filePath) {
        Map<Integer, Carte> colectieCarti = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Path path = Paths.get(filePath);
            String content = Files.readString(path);
            JsonNode jsonNode = objectMapper.readTree(content);

            colectieCarti = jsonToMap(jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return colectieCarti;
    }

    private static Map<Integer, Carte> jsonToMap(JsonNode jsonNode) {
        Map<Integer, Carte> colectieCarti = new HashMap<>();
        try {
            Iterator<Map.Entry<String, JsonNode>> iterator = jsonNode.fields();

            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                int id = Integer.parseInt(entry.getKey());
                JsonNode carteNode = entry.getValue();
                String titlu = carteNode.get("titlul").asText();
                String autor = carteNode.get("autorul").asText();
                int anAparitie = carteNode.get("anul").asInt();

                colectieCarti.put(id, new Carte(titlu, autor, anAparitie));
            }

            return colectieCarti;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    private static void afiseazaColectie(Map<Integer, Carte> colectieCarti) {
        System.out.println("Colectie:");
        colectieCarti.forEach((id, carte) -> System.out.println("ID: " + id + ", Carte: " + carte));
    }

    private static void stergeCarte(Map<Integer, Carte> colectieCarti, int id) {
        colectieCarti.remove(id);
        System.out.println("Cartea cu ID-ul " + id + " a fost stearsa.");
        afiseazaColectie(colectieCarti);
    }

    private static Carte citesteCarteNoua() {
        System.out.print("Introduceti titlul cartii noi: ");
        String titlu = scanner.next().trim();

        System.out.print("Introduceti numele autorului cartii noi: ");
        String autor = scanner.next().trim();

        System.out.print("Introduceti anul aparitiei cartii noi: ");
        int anAparitie = scanner.nextInt();

        return new Carte(titlu, autor, anAparitie);
    }

    private static void adaugaCarte(Map<Integer, Carte> colectieCarti, Carte carte) {
        colectieCarti.putIfAbsent(colectieCarti.size() + 1, carte);
        System.out.println("Cartea a fost adaugata.");
        afiseazaColectie(colectieCarti);
    }

    private static void salveazaModificariInJSON(Map<Integer, Carte> colectieCarti, String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(fileName), colectieCarti.values().toArray());
            System.out.println("Modificarile au fost salvate in " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void creeazaSiAfiseazaColectieAutor(Map<Integer, Carte> colectieCarti, String autor) {
        Set<Carte> cartiAutor = colectieCarti.values().stream()
                .filter(carte -> carte.autor().equals(autor))
                .collect(Collectors.toSet());

        System.out.println("Colectie pentru autorul " + autor + ":");
        cartiAutor.forEach(System.out::println);
    }

    private static void afiseazaColectieSortataDupaTitlu(Map<Integer, Carte> colectieCarti) {
        System.out.println("Colectie ordonata dupa titlu:");
        colectieCarti.values().stream()
                .sorted(Comparator.comparing(Carte::titlu))
                .forEach(System.out::println);
    }

    private static void afiseazaCeaMaiVecheCarte(Map<Integer, Carte> colectieCarti) {
        Optional<Carte> ceaMaiVecheCarte = colectieCarti.values().stream()
                .min(Comparator.comparingInt(Carte::anAparitie));

        ceaMaiVecheCarte.ifPresent(carte -> System.out.println("Cea mai veche carte: " + carte));
    }

    public static void main(String[] args)
    {
        Map<Integer, Carte> colectieCarti = citesteCartiDinJSON("src/main/resources/carti.json");

        do
        {
            System.out.println("1. Afiseaza colectia");
            System.out.println("2. sterge o carte din colectie");
            System.out.println("3. Adauga o carte in colectie");
            System.out.println("4. Salveaza modificarile in fisierul JSON");
            System.out.println("5. Creeaza colectie Set pentru autorul Yual Noah Harari si afiseaza");
            System.out.println("6. Afiseaza ordonat dupa titlu cartile din colectia Set");
            System.out.println("7. Afiseaza datele celei mai vechi carti din colectia Set");
            System.out.println("0. Iesire");

            System.out.print("Selectati o optiune: ");
            int optiune = scanner.nextInt();
            System.out.println();

            switch (optiune) {
                case 1:
                    afiseazaColectie(colectieCarti);
                    break;
                case 2:
                    System.out.print("Introduceti ID-ul cartii de sters: ");
                    int idStergere = scanner.nextInt();
                    stergeCarte(colectieCarti, idStergere);
                    break;
                case 3:
                    Carte carteNoua = citesteCarteNoua();
                    adaugaCarte(colectieCarti, carteNoua);
                    break;
                case 4:
                    salveazaModificariInJSON(colectieCarti, "carti_modificate.json");
                    break;
                case 5:
                    creeazaSiAfiseazaColectieAutor(colectieCarti, "Yuval Noah Harari");
                    break;
                case 6:
                    afiseazaColectieSortataDupaTitlu(colectieCarti);
                    break;
                case 7:
                    afiseazaCeaMaiVecheCarte(colectieCarti);
                    break;
                case 0:
                    System.exit(0);
                default:
                    System.out.println("Optiune invalida. Reincercati.");
            }
            System.out.println();
        } while (true);
    }
}
