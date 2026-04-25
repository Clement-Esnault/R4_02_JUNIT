# SimulateurImpot2024

Projet de réusinage (*refactoring*) d'un code hérité de simulation de l'impôt sur le revenu 2024 (revenus 2023), réalisé dans le cadre de la SAE R4.02 — Tests unitaires fonctionnels.

## Contexte

Le projet part d'un code hérité (`com.kerware.simulateur.Simulateur`) non documenté, sans tests et sans modularité, implémentant le calcul de l'impôt sur le revenu pour des contribuables célibataires, mariés, divorcés, veufs ou pacsés, avec ou sans enfants à charge.

L'objectif est :
1. D'écrire une batterie de tests unitaires fonctionnels servant de **filet de sécurité anti-régression** (Golden Master).
2. De **réusiner** le code hérité en un code lisible, modulaire et maintenable, sans changer le comportement.

## Structure du projet

```
src/
├── main/java/
│   ├── Calculateur/
│   │   ├── ICalculateurImpot.java       # Interface commune aux deux implémentations
│   │   └── SituationFamiliale.java      # Enum : CELIBATAIRE, MARIE, PACSE, DIVORCE, VEUF
│   ├── com/kerware/simulateur/
│   │   ├── Simulateur.java              # Code hérité (non modifié)
│   │   └── AdaptateurCodeHerite.java    # Adaptateur vers ICalculateurImpot
│   └── simulateurreusine/
│       ├── Simulateur.java              # Orchestrateur du code réusiné
│       ├── FoyerFiscal.java             # Données du foyer fiscal
│       ├── CalculateurAbattement.java   # Étape 1 : abattement 10 % (EXG_IMPOT_02)
│       ├── CalculateurQuotient.java     # Étape 2 : quotient familial (EXG_IMPOT_03)
│       ├── CalculateurBareme.java       # Étape 3 : barème progressif (EXG_IMPOT_04)
│       ├── CalculateurPlafonnement.java # Étape 4 : plafonnement QF (EXG_IMPOT_05)
│       ├── CalculateurDecote.java       # Étape 5 : décote (EXG_IMPOT_06)
│       └── AdaptateurCodeReusine.java   # Adaptateur vers ICalculateurImpot
└── test/java/
    └── com/kerware/simulateur/
        └── TestSimulateurImpot.java     # 36 tests fonctionnels (EXG-001 à EXG-020 + ERR-01 à ERR-04)
```

## Exigences couvertes

| Exigence | Description |
|---|---|
| EXG_IMPOT_01 | Arrondis à l'euro le plus proche |
| EXG_IMPOT_02 | Abattement de 10 % (plancher 495 €, plafond 14 171 €) |
| EXG_IMPOT_03 | Calcul du nombre de parts fiscales |
| EXG_IMPOT_04 | Barème progressif par tranches |
| EXG_IMPOT_05 | Plafonnement du quotient familial (1 759 € / demi-part) |
| EXG_IMPOT_06 | Décote pour revenus modestes |

## Prérequis

- Java 17
- Maven 3.8+

## Lancer les tests

```bash
mvn clean test
```

Pour basculer entre le code hérité et le code réusiné, modifier la constante `TYPE_CODE` dans `TestSimulateurImpot.java` :

```java
public static final int TYPE_CODE = CODE_REUSINE; // ou CODE_HERITE
```

## Générer les rapports qualité

```bash
mvn clean site
```

Les rapports sont disponibles dans `target/site/` :

- **Résultats des tests** : `target/site/surefire-report.html`
- **Couverture de code** : `target/site/jacoco/index.html`
- **Analyse statique** : `target/site/checkstyle.html`

## Résultats

| Indicateur | Résultat |
|---|---|
| Tests passants (code hérité) | 36 / 36 |
| Tests passants (code réusiné) | 36 / 36 |
| Couverture de lignes (code réusiné) | ≥ 90 % |
| CheckStyle (`iut-caen-checks.xml`) | 0 erreur |

## Auteur

Clément Esnault — IUT Caen, BUT Informatique S4, 2025-2026
