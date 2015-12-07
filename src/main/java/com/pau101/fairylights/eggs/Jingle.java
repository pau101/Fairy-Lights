package com.pau101.fairylights.eggs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Jingle {
	public static class PlayTick {
		private int[] notes;

		private int length;

		public PlayTick(int[] notes, int length) {
			this.notes = notes;
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public int[] getNotes() {
			return notes;
		}
	}

	private static final char PLAY_TICK_DELIMITER = ',';

	private static final char LENGTH_DELIMITER = '_';

	private static final char NOTE_DELIMITER = '&';

	private static final int DEFAULT_LENGTH = 2;

	private static final Random JINGLER = new Random();

	public static final String BACKUP_JINGLE = "11,17,10,10,17,13,13,13,15,11";

	public static Map<String, Jingle> jingles = new HashMap<String, Jingle>();

	private static int minRange, maxRange;

	private List<PlayTick> playTicks;

	private int minNote, maxNote;

	public Jingle() {
		playTicks = new ArrayList<PlayTick>();
		minNote = -1;
		maxNote = 0;
	}

	public void addPlayTick(PlayTick playTick) {
		playTicks.add(playTick);
	}

	public void calculateRange() {
		minNote = 24;
		maxNote = 0;
		for (PlayTick playTick : playTicks) {
			for (int note : playTick.notes) {
				if (note > maxNote) {
					maxNote = note;
				}
				if (note < minNote) {
					minNote = note;
				}
			}
		}
		if (minNote > maxNote) {
			minNote = -1;
			maxNote = 0;
		}
	}

	public int getLowestNote() {
		return minNote;
	}

	public int getNoteCount() {
		return playTicks.size();
	}

	public List<PlayTick> getPlayTicks() {
		return playTicks;
	}

	public int getRange() {
		return maxNote - minNote + 1;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		int mostCommonLength = 0, numberof = 0;
		for (int n = 0; n < playTicks.size(); n++) {
			PlayTick playTick = playTicks.get(n);
			for (int i = 0; i < playTick.notes.length; i++) {
				stringBuilder.append(playTick.notes[i]);
				if (i < playTick.notes.length - 1) {
					stringBuilder.append(NOTE_DELIMITER);
				}
			}
			stringBuilder.append(LENGTH_DELIMITER);
			stringBuilder.append(playTick.length);
			if (n < playTicks.size() - 1) {
				stringBuilder.append(PLAY_TICK_DELIMITER);
			}
		}
		return stringBuilder.toString();
	}

	public static Jingle getBackupJingle() {
		return Jingle.parse(BACKUP_JINGLE);
	}

	public static int getMaxRange() {
		return maxRange;
	}

	public static int getMinRange() {
		return minRange;
	}

	public static Jingle getRandomJingle(int range) {
		int totalWeight = 0;
		List<Jingle> possibleJingles = new ArrayList<Jingle>();
		for (Jingle jingle : jingles.values()) {
			int jingleRange = jingle.getRange();
			if (jingleRange <= range) {
				totalWeight += range - Math.abs(range - jingleRange);
				possibleJingles.add(jingle);
			}
		}
		float choice = JINGLER.nextFloat() * totalWeight;
		for (Jingle jingle : possibleJingles) {
			choice -= range - Math.abs(range - jingle.getRange());
			if (choice <= 0) {
				return jingle;
			}
		}
		return null;
	}

	public static void initJingles(String[] jingles) {
		Jingle.jingles.clear();
		minRange = Integer.MAX_VALUE;
		maxRange = Integer.MIN_VALUE;
		for (String jingle : jingles) {
			Jingle parsedJingle = Jingle.parse(jingle);
			int range = parsedJingle.getRange();
			if (range < minRange) {
				minRange = range;
			}
			if (range > maxRange) {
				maxRange = range;
			}
			Jingle.jingles.put(jingle, parsedJingle);
		}
	}

	public static Jingle parse(String string) {
		if (jingles.containsKey(string)) {
			return jingles.get(string);
		}
		Jingle jingle = new Jingle();
		String[] parts = string.split("" + PLAY_TICK_DELIMITER);
		for (String part : parts) {
			String[] noteEvent = part.split("" + LENGTH_DELIMITER);
			if (noteEvent.length == 0) {
				continue;
			}
			try {
				String note = noteEvent[0];
				int[] notes;
				if (note.indexOf(NOTE_DELIMITER) == -1) {
					notes = new int[] { Integer.parseInt(noteEvent[0]) };
				} else {
					String[] noteStrings = note.split("" + NOTE_DELIMITER);
					notes = new int[noteStrings.length];
					for (int i = 0; i < notes.length; i++) {
						notes[i] = Integer.parseInt(noteStrings[i]);
					}
				}
				jingle.addPlayTick(new PlayTick(notes, noteEvent.length == 1 ? DEFAULT_LENGTH : Integer.parseInt(noteEvent[1])));
			} catch (NumberFormatException e) {
				// just ignore the note
			}
		}
		if (jingle.getNoteCount() == 0) {
			jingle = getBackupJingle();
		}
		jingle.calculateRange();
		return jingle;
	}
}
