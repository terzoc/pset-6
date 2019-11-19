import java.io.IOException;
import java.util.Scanner;

public class ATM {
    
	private Scanner in;
	private BankAccount activeAccount;
	private Bank bank;
	    
	 public static final int VIEW = 1;
	 public static final int DEPOSIT = 2;
	 public static final int WITHDRAW = 3;
	 public static final int TRANSFER = 4;
	 public static final int LOGOUT = 5;
	 public static final int FIRST_NAME_WIDTH = 20;
	 public static final int LAST_NAME_WIDTH = 20;	
	 
	 public static final int INVALID = 0;
     public static final int INSUFFICIENT = 1;
     public static final int SUCCESS = 2; 
   
    ////////////////////////////////////////////////////////////////////////////
    //                                                                        //
    // Refer to the Simple ATM tutorial to fill in the details of this class. //
    // You'll need to implement the new features yourself.                    //
    //                                                                        //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a new instance of the ATM class.
     */
    	 
        public ATM() {
            in = new Scanner(System.in);
            
            activeAccount = new BankAccount(1234, 123456789, 0, new User("Ryan", "Wilson"));
            
            try {
    			this.bank = new Bank();
    		} catch (IOException e) {
    			// cleanup any resources (i.e., the Scanner) and exit
    		}
        }
        
        public void startup() {
        	long accountNo;
        	int pin;
            System.out.println("Welcome to the AIT ATM!\n");
            
            while (true) {
                System.out.print("Account No.: ");
                if(in.hasNextLong()) {
                	accountNo = in.nextLong();
                }else {
                	accountNo = 0;
                	in.nextLine();
                }
      
                System.out.print("PIN        : ");
                if(in.hasNextInt()) {
                	pin = in.nextInt();
                }else {
                	pin = 0;
                	in.nextLine();
                }
                
                if (isValidLogin(accountNo, pin)) {
                	activeAccount = bank.login(accountNo, pin);
                    System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
                    boolean validLogin = true;
                    while (validLogin) {
                        switch (getSelection()) {
                            case VIEW: showBalance(); break;
                            case DEPOSIT: deposit(); break;
                            case WITHDRAW: withdraw(); break;
                            case TRANSFER: transfer(); break;
                            case LOGOUT: validLogin = false; break;
                            default: System.out.println("\nInvalid selection.\n"); break;
                        }
                    }
                } else {
                    if (accountNo == -1 && pin == -1) {
                        shutdown();
                    } else {
                        System.out.println("\nInvalid account number and/or PIN.\n");
                    }
                }
            }
        }
        
        public boolean isValidLogin(long accountNo, int pin) {
        	boolean valid = false;
        	try {
        		valid = bank.login(accountNo, pin) != null ? true : false;
        	}catch (Exception e) {
        		valid = false;
        	}
        	
            return valid;
        }
        
        public int getSelection() {
            System.out.println("[1] View balance");
            System.out.println("[2] Deposit money");
            System.out.println("[3] Withdraw money");
            System.out.println("[4] Transfer money");
            System.out.println("[5] Logout");
            
            return in.nextInt();
        }
        
        public void showBalance() {
            System.out.println("\nCurrent balance: " + activeAccount.getBalance());
        }
        
        public void deposit() {
            System.out.print("\nEnter amount: ");
            double amount = in.nextDouble();
                
            int status = activeAccount.deposit(amount);
            if (status == ATM.INVALID) {
                System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
            } else if (status == ATM.SUCCESS) {
                System.out.println("\nDeposit accepted.\n");
            }
        }
            
        public void withdraw() {
    			double amount = 0;
    			boolean validAmount = true;
        		System.out.print("\nEnter amount: ");
        		try {
        			amount = in.nextDouble();
        		}catch(Exception e) {
        			validAmount = false;
        			in.nextLine();
        		}
        		if(validAmount) {
        			int status = activeAccount.withdraw(amount);
                    if (status == ATM.INVALID) {
                        System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
                    } else if (status == ATM.INSUFFICIENT) {
                        System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
                    } else if (status == ATM.SUCCESS) {
                        System.out.println("\nWithdrawal accepted.\n");
                    }
        		}else {
        			System.out.println("\nWithdrawal rejected. Enter vaild amount.\n");
        		}
         }
        
        public void transfer() {
        	boolean validAccount = true;
            System.out.print("\nEnter account: ");
            long secondedAccountNumber = in.nextLong();
            System.out.print("Enter amount: ");
            double amount = in.nextDouble();
            if(bank.getAccount(secondedAccountNumber) == null) {
            	validAccount = false;
            }
            if(validAccount) {
            	BankAccount transferAccount = bank.getAccount(secondedAccountNumber);
            	int withdrawStatus = activeAccount.withdraw(amount);
            	if (withdrawStatus == ATM.INVALID) {
                    System.out.println("\nTransfer rejected. Amount must be greater than $0.00.\n");
                } else if (withdrawStatus == ATM.INSUFFICIENT) {
                    System.out.println("\nTransfer rejected. Insufficient funds.\n");
                } else if (withdrawStatus == ATM.SUCCESS) {
                	int depositStatus = transferAccount.deposit(amount);
                    if (depositStatus == ATM.INVALID) {
                        System.out.println("\nTransfer rejected. Amount must be greater than $0.00.\n");
                    } else if (depositStatus == ATM.SUCCESS) {
                    	System.out.println("\nTransfer accepted.\n");
                    }
                }
            }else {
            	System.out.println("\nTransfer rejected. Invalid account .\n");
            }
            
        }
            
        public void shutdown() {
            if (in != null) {
                in.close();
            }
            
            System.out.println("\nGoodbye!");
            System.exit(0);
        }
        
    
    
    /*
     * Application execution begins here.
     */
    
    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.startup();
    }
}
