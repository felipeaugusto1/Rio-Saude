package riomaissaude.felipe.com.br.riosaude.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by felipe on 9/15/15.
 */
public class SearchableProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "riomaissaude.felipe.com.br.riomaissaude.provider.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchableProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
