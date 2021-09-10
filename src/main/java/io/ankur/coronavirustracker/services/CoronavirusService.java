package io.ankur.coronavirustracker.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.ankur.coronavirustracker.models.LocationStats;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
public class CoronavirusService {

    private static String url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/" +
            "csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> locationStats;

    private int totalNumberOfCases;

    @PostConstruct
    public void fetchData() throws IOException, InterruptedException, CsvException {
        HttpClient client =  HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<LocationStats> locationStats = parseCsv(response.body());
        this.setTotalNumberOfCases(locationStats.stream().mapToInt(l -> l.getLatestTotalCases()).sum());
        this.setLocationStats(locationStats);
    }

    private List<LocationStats> parseCsv(String csv) throws IOException, CsvException {
        List<LocationStats> locationStats = new ArrayList<>();
        CSVReader csvReader = new CSVReader(new StringReader(csv));
        List<String[]> records = csvReader.readAll();
        records = records.subList(1,records.size());
        for(String[] record : records){
            locationStats.add(LocationStats.builder()
                    .state(record[0])
                    .country(record[1])
                    .diffFromPrevDay(Integer.parseInt(record[records.size()-1]) - Integer.parseInt(record[records.size()-2]))
                    .latestTotalCases(Integer.parseInt(record[records.size()-1]))
                    .build());
        }
        return locationStats;
    }

}
