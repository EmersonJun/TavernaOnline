package br.com.tavernaonline.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class ListaInteirosConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> valores) {
        if (valores == null || valores.isEmpty()) {
            return "";
        }
        return valores.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public List<Integer> convertToEntityAttribute(String coluna) {
        if (coluna == null || coluna.isBlank()) {
            return List.of();
        }
        return Arrays.stream(coluna.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }
}
