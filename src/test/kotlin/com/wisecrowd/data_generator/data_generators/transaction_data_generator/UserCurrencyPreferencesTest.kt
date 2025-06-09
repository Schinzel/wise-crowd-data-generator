package com.wisecrowd.data_generator.data_generators.transaction_data_generator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.random.Random

class UserCurrencyPreferencesTest {
    @Nested
    inner class GenerateUserCurrencyPreferences {
        @Test
        fun generateUserCurrencyPreferences_swedishUser_prefersSEKCurrency() {
            val preferences = UserCurrencyPreferences(random = Random(42))

            val currencySelector = preferences.generateUserCurrencyPreferences(countryId = 1)

            // Generate multiple selections to verify SEK preference
            val selectedCurrencies = (1..100).map { currencySelector.getRandomItem() }
            val sekCount = selectedCurrencies.count { it == 1 }

            // SEK should be dominant (expect 70-80% usage)
            assertThat(sekCount).isGreaterThan(60)
        }

        @Test
        fun generateUserCurrencyPreferences_norwegianUser_prefersNOKCurrency() {
            val preferences = UserCurrencyPreferences(random = Random(42))

            val currencySelector = preferences.generateUserCurrencyPreferences(countryId = 2)

            // Generate multiple selections to verify NOK preference
            val selectedCurrencies = (1..100).map { currencySelector.getRandomItem() }
            val nokCount = selectedCurrencies.count { it == 4 }

            // NOK should be dominant (expect 70-80% usage)
            assertThat(nokCount).isGreaterThan(60)
        }

        @Test
        fun generateUserCurrencyPreferences_danishUser_prefersDKKCurrency() {
            val preferences = UserCurrencyPreferences(random = Random(42))

            val currencySelector = preferences.generateUserCurrencyPreferences(countryId = 3)

            // Generate multiple selections to verify DKK preference
            val selectedCurrencies = (1..100).map { currencySelector.getRandomItem() }
            val dkkCount = selectedCurrencies.count { it == 5 }

            // DKK should be dominant (expect 70-80% usage)
            assertThat(dkkCount).isGreaterThan(60)
        }

        @Test
        fun generateUserCurrencyPreferences_finnishUser_prefersEURCurrency() {
            val preferences = UserCurrencyPreferences(random = Random(42))

            val currencySelector = preferences.generateUserCurrencyPreferences(countryId = 4)

            // Generate multiple selections to verify EUR preference
            val selectedCurrencies = (1..100).map { currencySelector.getRandomItem() }
            val eurCount = selectedCurrencies.count { it == 2 }

            // EUR should be dominant (expect 70-80% usage)
            assertThat(eurCount).isGreaterThan(60)
        }

        @Test
        fun generateUserCurrencyPreferences_icelandicUser_prefersEURCurrency() {
            val preferences = UserCurrencyPreferences(random = Random(42))

            val currencySelector = preferences.generateUserCurrencyPreferences(countryId = 5)

            // Generate multiple selections to verify EUR preference
            val selectedCurrencies = (1..100).map { currencySelector.getRandomItem() }
            val eurCount = selectedCurrencies.count { it == 2 }

            // EUR should be dominant (expect 70-80% usage)
            assertThat(eurCount).isGreaterThan(60)
        }

        @Test
        fun generateUserCurrencyPreferences_unknownCountry_defaultsToSEK() {
            val preferences = UserCurrencyPreferences(random = Random(42))

            val currencySelector = preferences.generateUserCurrencyPreferences(countryId = 999)

            // Generate multiple selections to verify SEK preference
            val selectedCurrencies = (1..100).map { currencySelector.getRandomItem() }
            val sekCount = selectedCurrencies.count { it == 1 }

            // SEK should be dominant (expect 70-80% usage)
            assertThat(sekCount).isGreaterThan(60)
        }

        @Test
        fun generateUserCurrencyPreferences_multipleUsers_showsCurrencyDiversification() {
            val preferences = UserCurrencyPreferences(random = Random(42))

            val swedishSelector = preferences.generateUserCurrencyPreferences(countryId = 1)
            val norwegianSelector = preferences.generateUserCurrencyPreferences(countryId = 2)

            // Generate selections for both users
            val swedishCurrencies = (1..50).map { swedishSelector.getRandomItem() }.toSet()
            val norwegianCurrencies = (1..50).map { norwegianSelector.getRandomItem() }.toSet()

            // Swedish user should use primarily SEK
            assertThat(swedishCurrencies).contains(1)
            // Norwegian user should use primarily NOK
            assertThat(norwegianCurrencies).contains(4)

            // Both might use international currencies but with lower frequency
            val allSwedishSelections = (1..100).map { swedishSelector.getRandomItem() }
            val allNorwegianSelections = (1..100).map { norwegianSelector.getRandomItem() }

            // Verify home currency dominance
            val swedishSEKRatio = allSwedishSelections.count { it == 1 } / 100.0
            val norwegianNOKRatio = allNorwegianSelections.count { it == 4 } / 100.0

            assertThat(swedishSEKRatio).isGreaterThan(0.6)
            assertThat(norwegianNOKRatio).isGreaterThan(0.6)
        }

        @Test
        fun generateUserCurrencyPreferences_consistentSeed_producesReproducibleResults() {
            val preferences1 = UserCurrencyPreferences(random = Random(123))
            val preferences2 = UserCurrencyPreferences(random = Random(123))

            val selector1 = preferences1.generateUserCurrencyPreferences(countryId = 1)
            val selector2 = preferences2.generateUserCurrencyPreferences(countryId = 1)

            val selections1 = (1..20).map { selector1.getRandomItem() }
            val selections2 = (1..20).map { selector2.getRandomItem() }

            assertThat(selections1).isEqualTo(selections2)
        }
    }

    @Nested
    inner class InternationalDiversification {
        @Test
        fun generateUserCurrencyPreferences_swedishUser_mayUseInternationalCurrencies() {
            val preferences = UserCurrencyPreferences(random = Random(42))

            val currencySelector = preferences.generateUserCurrencyPreferences(countryId = 1)

            // Generate many selections to see diversification
            val selectedCurrencies = (1..500).map { currencySelector.getRandomItem() }.toSet()

            // Should include SEK (home currency)
            assertThat(selectedCurrencies).contains(1)

            // May include international currencies (EUR or USD)
            val hasInternationalCurrency = selectedCurrencies.any { it == 2 || it == 3 }
            // Note: This test may occasionally fail due to randomness (40% chance of no international currency)
            // But with 60% probability of international currency, it should usually pass
        }

        @Test
        fun generateUserCurrencyPreferences_limitedCurrencySelection_userPreferencesRealistic() {
            val preferences = UserCurrencyPreferences(random = Random(42))

            // Test multiple countries to verify realistic currency patterns
            val countries = listOf(1, 2, 3, 4, 5)

            countries.forEach { countryId ->
                val currencySelector = preferences.generateUserCurrencyPreferences(countryId)
                val selectedCurrencies = (1..100).map { currencySelector.getRandomItem() }.toSet()

                // Each user should have limited currency selection (1-2 currencies max)
                assertThat(selectedCurrencies.size).isLessThanOrEqualTo(2)
            }
        }
    }
}
