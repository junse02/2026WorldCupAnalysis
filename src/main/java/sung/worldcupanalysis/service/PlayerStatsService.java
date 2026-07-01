package sung.worldcupanalysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import sung.worldcupanalysis.model.PlayerStat;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

/**
 * Serves the tournament scoring leaderboard. Currently backed by a curated
 * snapshot ({@code sample/player-stats.json}) so it works without an API key,
 * mirroring the rest of the app's live-or-fallback design. Players are ranked by
 * goals, then assists — the adidas Golden Boot tiebreaker.
 */
@Service
public class PlayerStatsService {

    private static final Logger log = LoggerFactory.getLogger(PlayerStatsService.class);
    private static final String RESOURCE = "sample/player-stats.json";

    private final List<PlayerStat> topScorers;

    public PlayerStatsService() {
        this.topScorers = load();
    }

    /** Leaderboard ordered by goals, then assists (Golden Boot tiebreaker). */
    public List<PlayerStat> topScorers() {
        return topScorers;
    }

    private List<PlayerStat> load() {
        try (InputStream in = new ClassPathResource(RESOURCE).getInputStream()) {
            PlayersFile file = new ObjectMapper().readValue(in, PlayersFile.class);
            List<PlayerStat> players = file.players().stream()
                    .sorted(Comparator.comparingInt(PlayerStat::goals).reversed()
                            .thenComparing(Comparator.comparingInt(PlayerStat::assists).reversed())
                            .thenComparing(PlayerStat::name))
                    .toList();
            log.info("Loaded {} curated player stats.", players.size());
            return players;
        } catch (Exception e) {
            log.error("Failed to load curated player stats from {}", RESOURCE, e);
            return List.of();
        }
    }

    private record PlayersFile(List<PlayerStat> players) {
    }
}
