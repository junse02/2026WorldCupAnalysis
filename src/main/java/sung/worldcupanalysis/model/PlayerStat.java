package sung.worldcupanalysis.model;

/**
 * A single player's tournament tally, used for the scoring leaderboard.
 * {@code team} is the English nation name (so it matches the flag lookup and the
 * rest of the app), while {@code name} is the Korean display name.
 */
public record PlayerStat(String name, String team, int goals, int assists) {
}
