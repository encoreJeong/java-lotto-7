package lotto;

import lotto.console.ConsoleOutput;
import lotto.domain.Lotto;
import lotto.domain.LottoGame;
import lotto.domain.LottoNumber;
import lotto.util.StringMaker;

import java.util.List;

public class LottoGameRunner {

    private final LottoGame lottoGame;

    private LottoGameRunner(LottoGame lottoGame) {
        this.lottoGame = lottoGame;
    }

    public static LottoGameRunner from(LottoGame lottoGame) {
        return new LottoGameRunner(lottoGame);
    }

    public void run() {

        List<Integer> wins = calculateRankFromMatchResult(matchLottoNumbers());

        Double earningRate = calculateEarningRate(wins);

        printLottoGameResult(wins, earningRate);
    }

    private void printLottoGameResult(List<Integer> winningRanks, Double earningRate) {
        ConsoleOutput.print(makeWinStatusResult(winningRanks, earningRate));
    }

    private static String makeWinStatusResult(List<Integer> winningRanks, Double earningRate) {

        int[] countPerWinningRank = countRanks(winningRanks);

        return StringMaker.make(earningRate, countPerWinningRank);
    }

    private static int[] countRanks(List<Integer> winningRanks) {
        int[] countPerWinningRank = new int[6];
        winningRanks
                .forEach(rank -> {
                    countPerWinningRank[rank]++;
                });
        return countPerWinningRank;
    }

    private Double calculateEarningRate(List<Integer> winningRanks) {

        double earnRate = ((double) calculateTotalEarning(winningRanks) / lottoGame.getTotalPrice().getValue()) * 100;

        return roundToSecondDecimalPlace(earnRate);
    }

    private double roundToSecondDecimalPlace(double earnRate) {
        return Math.round(earnRate * 100) / 100.0;
    }

    private Integer calculateTotalEarning(List<Integer> winningRanks) {
        return winningRanks.stream()
                .mapToInt(LottoGameRunner::winPriceFromRank)
                .sum();
    }

    private static int winPriceFromRank(Integer rank) {
        if(rank == 1) {
            return 2000000000;
        }

        if(rank == 2) {
            return 30000000;
        }

        if(rank == 3) {
            return 1500000;
        }

        if(rank == 4) {
            return 50000;
        }

        if(rank == 5) {
            return 5000;
        }

        return 0;
    }

    private List<Integer> calculateRankFromMatchResult(List<Integer> winningHistoty) {
        return winningHistoty.stream()
                .map(LottoGameRunner::gradeFromWinCount)
                .toList();
    }

    private static int gradeFromWinCount(Integer winCount) {
        if(winCount == 6) {
            return 1;
        }
        if(winCount == 7) {
            return 2;
        }
        if(winCount == 5) {
            return 3;
        }
        if(winCount == 4) {
            return 4;
        }
        if(winCount == 3) {
            return 5;
        }
        return 0;
    }

    private List<Integer> matchLottoNumbers() {
        return lottoGame.getPurchasedLottos().getValue().stream()
                .map(this::countMatchedNumbers)
                .toList();
    }

    private int countMatchedNumbers(Lotto lotto) {
        int correctCount = 0;

        for(LottoNumber value : lotto.getValue()) {
            if(lottoGame.getWinningNumbers().contains(value)) {
                correctCount++;
            }
        }

        if (correctCount == 5) {
            if (lotto.contains(lottoGame.getBonusNumber().getValue())) {
                correctCount = 7;
            }
        }
        return correctCount;
    }
}
