package it.unibo.oop.lab.streams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final static int DEFAULT_SONG_WEIGHT = 1;

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream()
            .map(Song::getSongName)
            .sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet()
            .stream()
            .filter(o1 -> o1.getValue().equals(year))
            .map(o1 -> o1.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return this.songs.stream()
            .filter(o1 -> o1.getAlbumName().equals(Optional.ofNullable(albumName)))
            .mapToInt(o1 -> Integer.valueOf(DEFAULT_SONG_WEIGHT))
            .sum();
    }

    @Override
    public int countSongsInNoAlbum() {
        return this.songs.stream()
            .filter(o1 -> o1.getAlbumName().equals(Optional.empty()))
            .mapToInt(o1 -> Integer.valueOf(DEFAULT_SONG_WEIGHT))
            .sum();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream()
            .filter(o1 -> o1.getAlbumName().equals(Optional.ofNullable(albumName)))
            .mapToDouble(Song::getDuration)
            .average();
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream()
            .max((o1,o2) -> Double.compare(o1.getDuration(),o2.getDuration()))
            .map(Song::getSongName);
    }

    @Override
    public Optional<String> longestAlbum() {
        return this.albums.keySet().stream()
            .max(String::compareTo);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return this.songName;
        }

        public Optional<String> getAlbumName() {
            return this.albumName;
        }

        public double getDuration() {
            return this.duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
