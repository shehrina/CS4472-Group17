package atm.session;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import atm.ATM;
import atm.dispatcher.MessageDispatcher;
import atm.exceptions.InvalidAmountException;
import atm.exceptions.InvalidPinFormatException;
import atm.ui.panels.MainPanel;
import atm.utils.CredentialsCheck;
import atm.utils.FormatChecker;
import bank.transactions.utils.AccountType;
import bank.transactions.utils.TransactionResult;
import bank.transactions.utils.TransactionType;

// Session tests: PIN format + withdrawal amount (Robust Worst Case BVA)
class SessionTest {
	ATM atm;

	@BeforeEach
	void setUp() {
		// Fake bank: account with $5000 balance, $1000 daily limit
		MessageDispatcher bank = Mockito.mock(MessageDispatcher.class);
		Mockito.when(bank.checkCredentials(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
		Mockito.when(bank.performTransaction(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(new TransactionResult(true, "", 0, new double[] {5000}));

		atm = new ATM(new FormatChecker(), new CredentialsCheck(bank), bank);
		atm.setMainPanel(Mockito.mock(MainPanel.class));
		atm.createSession();
	}

	// expected: OK = no error, PIN = InvalidPinFormatException, AMOUNT = InvalidAmountException
	@ParameterizedTest(name = "{0}: pin={1}, amount={2}, expected={3}")
	@CsvSource({
		"S01, -0001, 10, PIN",
		"S02, -0001, 20, PIN",
		"S03, -0001, 40, PIN",
		"S04, -0001, 500, PIN",
		"S05, -0001, 990, PIN",
		"S06, -0001, 1000, PIN",
		"S07, -0001, 1010, PIN",
		"S08, 00000, 10, AMOUNT",
		"S09, 00000, 20, OK",
		"S10, 00000, 40, OK",
		"S11, 00000, 500, OK",
		"S12, 00000, 990, OK",
		"S13, 00000, 1000, OK",
		"S14, 00000, 1010, AMOUNT",
		"S15, 00001, 10, AMOUNT",
		"S16, 00001, 20, OK",
		"S17, 00001, 40, OK",
		"S18, 00001, 500, OK",
		"S19, 00001, 990, OK",
		"S20, 00001, 1000, OK",
		"S21, 00001, 1010, AMOUNT",
		"S22, 50000, 10, AMOUNT",
		"S23, 50000, 20, OK",
		"S24, 50000, 40, OK",
		"S25, 50000, 500, OK",
		"S26, 50000, 990, OK",
		"S27, 50000, 1000, OK",
		"S28, 50000, 1010, AMOUNT",
		"S29, 99998, 10, AMOUNT",
		"S30, 99998, 20, OK",
		"S31, 99998, 40, OK",
		"S32, 99998, 500, OK",
		"S33, 99998, 990, OK",
		"S34, 99998, 1000, OK",
		"S35, 99998, 1010, AMOUNT",
		"S36, 99999, 10, AMOUNT",
		"S37, 99999, 20, OK",
		"S38, 99999, 40, OK",
		"S39, 99999, 500, OK",
		"S40, 99999, 990, OK",
		"S41, 99999, 1000, OK",
		"S42, 99999, 1010, AMOUNT",
		"S43, 100000, 10, PIN",
		"S44, 100000, 20, PIN",
		"S45, 100000, 40, PIN",
		"S46, 100000, 500, PIN",
		"S47, 100000, 990, PIN",
		"S48, 100000, 1000, PIN",
		"S49, 100000, 1010, PIN"
	})
	void testWithdrawal(String id, String pin, int amount, String expected) throws Exception {
		atm.checkCardNumber("4000000000000000");

		// Step 1: enter PIN
		if (expected.equals("PIN")) {
			assertThrows(InvalidPinFormatException.class, () -> atm.checkPin(pin.toCharArray()));
			return;
		}
		assertDoesNotThrow(() -> atm.checkPin(pin.toCharArray()));

		// Step 2: choose withdrawal (choice 1) from chequing
		atm.setTransaction(TransactionType.Withdrawal);
		atm.setFromAccount(AccountType.Chequing);

		// Step 3: enter amount
		if (expected.equals("AMOUNT")) {
			assertThrows(InvalidAmountException.class, () -> atm.setAmount(amount));
		} else {
			assertDoesNotThrow(() -> atm.setAmount(amount));
		}
	}
}
