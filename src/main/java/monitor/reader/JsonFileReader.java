package monitor.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import monitor.model.Trade;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JsonFileReader {

    public List<List<Trade>> getTrades(String directory) throws IOException {
        List<File> files = readJsonFiles(directory);
        return files.stream()
                .map(this::readFileAsString)
                .map(this::convertJsonStringToTrade)
                .collect(Collectors.toList());
    }

    private List<File> readJsonFiles(String directory) throws IOException {
        return Files.walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
    }

    private String readFileAsString(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Files.lines(file.toPath(), StandardCharsets.UTF_8).forEach(s -> {
                stringBuilder.append(s).append("\n");
            });
        } catch (IOException e) {
            log.error("Unable to read file");
        }
        return stringBuilder.toString();
    }

    private List<Trade> convertJsonStringToTrade(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        Trade[] trades = new Trade[0];
        try {
            trades = objectMapper.readValue(json, Trade[].class);
        } catch (JsonProcessingException e) {
            log.error("Unable to convert json to object Trade");
        }
        return Arrays.asList(trades);
    }

}
