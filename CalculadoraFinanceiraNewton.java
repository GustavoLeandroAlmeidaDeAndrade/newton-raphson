import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculadoraFinanceiraNewton extends JFrame {

    private JTextField txtInvestimento, txtVPLAlvo, txtFluxos;
    private JTextArea txtResultado;
    private static final DecimalFormat Moeda = new DecimalFormat("R$ #,##0.00");
    private static final DecimalFormat Porcentagem = new DecimalFormat("0.00%");
    
    private static class ResultadoNewton {
        double taxa;
        int iteracoes;

        ResultadoNewton(double taxa, int iteracoes) {
            this.taxa = taxa;
            this.iteracoes = iteracoes;
        }
    }
    
    public CalculadoraFinanceiraNewton() {
        setTitle("Assistente de Investimentos - Método de Newton");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();

        // --- ABA 1: CALCULADORA ---
        JPanel painelCalculo = new JPanel(new BorderLayout(15, 15));
        painelCalculo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Área de entrada de dados
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.add(new JLabel("Quanto você vai investir hoje? (R$)"));
        txtInvestimento = new JTextField("120000.00");
        form.add(txtInvestimento);

        form.add(new JLabel("Quanto de lucro extra (VPL) você deseja? (R$)"));
        txtVPLAlvo = new JTextField("10000.00");
        form.add(txtVPLAlvo);

        form.add(new JLabel("Retornos mensais (separe por vírgula):"));
        txtFluxos = new JTextField("25000, 40000, 45000, 30000");
        form.add(txtFluxos);

        JButton btnCalcular = new JButton("Descobrir Taxa de Rendimento");
        btnCalcular.setFont(new Font("Arial", Font.BOLD, 14));
        btnCalcular.setBackground(new Color(46, 204, 113));
        btnCalcular.setForeground(Color.black);
        form.add(new JLabel("")); // Espaço
        form.add(btnCalcular);

        // Área de resultado
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(txtResultado);

        painelCalculo.add(form, BorderLayout.NORTH);
        painelCalculo.add(scroll, BorderLayout.CENTER);

        // --- ABA 2: EXPLICAÇÃO ---
        JPanel painelAjuda = new JPanel(new BorderLayout());
        JEditorPane textoAjuda = new JEditorPane("text/html", 
            "<html><body style='font-family: sans-serif; padding: 15px;'>" +
            "<h2>O que é o Método de Newton-Raphson?</h2>" +
            "<p>Imagine que você quer encontrar o 'ponto de equilíbrio' de um investimento, mas a conta matemática é tão complexa que não dá para isolar o 'i' (taxa) na fórmula.</p>" +
            "<p><b>O Método de Newton funciona como um 'Chute Inteligente':</b></p>" +
            "<ul>" +
            "<li><b>1. O Chute Inicial:</b> O programa começa com uma taxa qualquer (ex: 10%).</li>" +
            "<li><b>2. A Inclinação:</b> Ele olha para a curva do gráfico e vê se o resultado está subindo ou descendo rápido (através da <i>derivada</i>).</li>" +
            "<li><b>3. O Ajuste:</b> Usando essa inclinação, ele 'pula' para um novo chute muito mais próximo do real.</li>" +
            "<li><b>4. A Repetição:</b> Ele faz isso várias vezes em milissegundos até que a diferença seja quase zero.</li>" +
            "</ul>" +
            "<p>Em finanças, usamos isso para achar a <b>TIR</b> ou, como neste caso, a taxa exata para que seu lucro (VPL) seja exatamente o que você planejou.</p>" +
            "</body></html>"
        );
        textoAjuda.setEditable(false);
        painelAjuda.add(new JScrollPane(textoAjuda));

        // Adicionar abas
        abas.addTab("Calculadora de Rentabilidade", painelCalculo);
        abas.addTab("Como funciona a matemática?", painelAjuda);

        add(abas);

        // Evento do botão
        btnCalcular.addActionListener(e -> realizarCalculo());
    }

    private void realizarCalculo() {
        try {
            double investimento = Double.parseDouble(txtInvestimento.getText().replace(",", "."));
            double vplDesejado = Double.parseDouble(txtVPLAlvo.getText().replace(",", "."));
            
            String[] partes = txtFluxos.getText().split(",");
            double[] fluxos = new double[partes.length];
            for (int i = 0; i < partes.length; i++) {
                fluxos[i] = Double.parseDouble(partes[i].trim().replace(",", "."));
            }

            // Cálculo da taxa pelo método de Newton
            double alvoTotal = investimento + vplDesejado;
            ResultadoNewton resultadoNewton = calcularNewton(fluxos, alvoTotal);

            double taxaDecimal = resultadoNewton.taxa;
            int numeroIteracoes = resultadoNewton.iteracoes;

            // Saída amigável
            String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
            txtResultado.setText("RESUMO DO SEU INVESTIMENTO (" + hora + ")\n");
            txtResultado.append("----------------------------------------------------------------------\n");
            txtResultado.append("• Se você investir " + Moeda.format(investimento) + " agora...\n");
            txtResultado.append("• E receber os " + fluxos.length + " pagamentos informados...\n\n");
            
            txtResultado.append("RESULTADO:\n");
            txtResultado.append("Para que você tenha um lucro líquido de " + Moeda.format(vplDesejado) + ",\n");
            txtResultado.append("este projeto precisa render uma TAXA MENSAL de: " + Porcentagem.format(taxaDecimal) + "\n\n");
            txtResultado.append("Número de iterações realizadas: " + numeroIteracoes + "\n\n");
            txtResultado.append("O QUE ISSO SIGNIFICA?\n");
            txtResultado.append("Significa que o rendimento deste projeto equivale a um investimento \n" +
                               "que paga " + Porcentagem.format(taxaDecimal) + " de juros todo mês sobre o saldo restante.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Por favor, use apenas números e vírgulas para os fluxos.", "Erro nos dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ResultadoNewton calcularNewton(double[] fluxos, double valorAlvo) {
        double i = 0.1; // Chute inicial de 10%

        System.out.println("\n========== MÉTODO DE NEWTON-RAPHSON ==========");
        System.out.printf("Valor-alvo: R$ %.2f%n", valorAlvo);
        System.out.printf("Taxa inicial: %.8f%%%n", i * 100);
        System.out.println("------------------------------------------------");

        for (int iter = 0; iter < 100; iter++) {
            double f = -valorAlvo;
            double df = 0;

            for (int t = 0; t < fluxos.length; t++) {
                double tempo = t + 1;
                double pot = Math.pow(1 + i, tempo);

                f += fluxos[t] / pot;
                df -= (tempo * fluxos[t]) / (pot * (1 + i));
            }

            // Exibe os dados da iteração somente no console
            System.out.printf(
                "Iteração %d | Taxa atual: %.8f%% | f(i): %.8f | Derivada: %.8f%n",
                iter + 1,
                i * 100,
                f,
                df
            );

            // Evita divisão por zero ou derivada muito próxima de zero
            if (Math.abs(df) < 1e-15) {
                System.out.println("ERRO: Derivada próxima de zero.");
                System.out.println("================================================\n");

                throw new ArithmeticException(
                    "Não foi possível continuar: derivada próxima de zero."
                );
            }

            double proximoI = i - (f / df);

            if (!Double.isFinite(proximoI) || proximoI <= -1) {
                System.out.println("ERRO: A taxa calculada não é válida.");
                System.out.println("================================================\n");

                throw new ArithmeticException(
                    "A taxa calculada não é válida."
                );
            }

            System.out.printf(
                "           Próxima taxa: %.8f%% | Diferença: %.10f%n",
                proximoI * 100,
                Math.abs(proximoI - i)
            );

            // Verifica se o método convergiu
            if (Math.abs(proximoI - i) < 1e-10) {
                System.out.printf(
                    "CONVERGÊNCIA! Taxa final: %.8f%%%n",
                    proximoI * 100
                );
                System.out.printf(
                    "Total de iterações: %d%n",
                    iter + 1
                );
                System.out.println("================================================\n");

                return new ResultadoNewton(proximoI, iter + 1);
            }

            i = proximoI;
        }

        System.out.println("ERRO: O método não convergiu após 100 iterações.");
        System.out.println("================================================\n");

        throw new ArithmeticException(
            "O método não convergiu após 100 iterações."
        );
    }
    
    public static void main(String[] args) {
        // Deixa a interface com a aparência do sistema operacional
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> new CalculadoraFinanceiraNewton().setVisible(true));
    }
}