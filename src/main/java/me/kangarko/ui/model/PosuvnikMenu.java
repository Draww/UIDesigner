package me.kangarko.ui.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.kangarko.ui.util.DesignerUtils;

/**
 * Slovak detected, sorry guys!
 *
 * @author kangarko
 */
public class PosuvnikMenu<T> {

	// Strana, Hodnoty
	private final Map<Integer, List<T>> strany;

	private final int velkost_bunky;

	public PosuvnikMenu(int velkost_bunky, Iterable<T> hodnoty) {
		this.velkost_bunky = velkost_bunky;
		this.strany = naplnStrany(velkost_bunky, DesignerUtils.toList(hodnoty));
	}

	private final Map<Integer, List<T>> naplnStrany(int velkost_bunky, List<T> hodnoty) {
		final Map<Integer, List<T>> strany = new HashMap<>();
		final int pocet_stran = hodnoty.size() == velkost_bunky ? 0 : hodnoty.size() / velkost_bunky;

		for (int cislo_strany = 0; cislo_strany <= pocet_stran; cislo_strany++) {
			final List<T> hodnoty_v_strane = new ArrayList<>();

			final int dole = velkost_bunky * cislo_strany;
			final int hore = dole + velkost_bunky;

			for (int index_hodnoty = dole; index_hodnoty < hore; index_hodnoty++) {
				if (index_hodnoty < hodnoty.size()) {
					final T strana = hodnoty.get(index_hodnoty);

					hodnoty_v_strane.add( strana );
				}

				else break;
			}

			strany.put(cislo_strany, hodnoty_v_strane);
		}

		return strany;
	}

	public final Map<Integer, List<T>> ziskatStrany() {
		return strany;
	}

	public final int ziskatVelkostBunky() {
		return velkost_bunky;
	}

	public static final <T> Map<Integer, List<T>> strany(int velkost_bunky, Iterable<T> hodnoty) {
		return new PosuvnikMenu<>(velkost_bunky, hodnoty).ziskatStrany();
	}
}
