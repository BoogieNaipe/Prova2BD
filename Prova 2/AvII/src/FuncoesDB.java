import java.sql.*;
import java.math.BigDecimal;

public class FuncoesDB {

    public Connection conectar_DB(String nome_DB, String user, String pass) {
        Connection conexao_DB = null;
        try {
            Class.forName("org.postgresql.Driver");
            conexao_DB = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + nome_DB, user, pass);
            if (conexao_DB != null) {
                System.out.println("Conectado com sucesso!");
            } else {
                System.out.println("Erro ao conectar ao Banco de Dados!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar ao Banco de Dados: " + e.getMessage());
        }
        return conexao_DB;
    }

    public void criar_tabela(Connection conexao_DB) {
        String[] queries = {
                "CREATE TABLE IF NOT EXISTS Aluno (matricula SERIAL PRIMARY KEY, nome_aluno VARCHAR(100) NOT NULL, endereco VARCHAR(200) NOT NULL)",
                "CREATE TABLE IF NOT EXISTS Professor (cod_professor SERIAL PRIMARY KEY, nome_professor VARCHAR(100), endereco VARCHAR(200))",
                "CREATE TABLE IF NOT EXISTS Curso (cod_curso SERIAL PRIMARY KEY, nome_curso VARCHAR(100))",
                "CREATE TABLE IF NOT EXISTS Disciplina (cod_disciplina SERIAL PRIMARY KEY, nome_disciplina VARCHAR(100))",
                "CREATE TABLE IF NOT EXISTS Turma (cod_turma SERIAL PRIMARY KEY, nome_turma VARCHAR(100), cod_curso INTEGER REFERENCES Curso(cod_curso))",
                "CREATE TABLE IF NOT EXISTS Avaliacao (cod_avaliacao SERIAL PRIMARY KEY, matricula INTEGER REFERENCES Aluno(matricula), cod_disciplina INTEGER REFERENCES Disciplina(cod_disciplina), nota NUMERIC)",
                "CREATE TABLE IF NOT EXISTS Turma_Disciplina (cod_disciplina INTEGER REFERENCES Disciplina(cod_disciplina), cod_turma INTEGER REFERENCES Turma(cod_turma), PRIMARY KEY (cod_disciplina, cod_turma))"
        };

        try (Statement stmt = conexao_DB.createStatement()) {
            for (String query : queries) {
                stmt.executeUpdate(query);
            }
            System.out.println("Tabelas criadas com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }

    public void inserir_linha_Aluno(Connection conexao_DB, String nome_Tabela, String nome_aluno, String endereco) {
        String query = String.format("INSERT INTO %s(nome_aluno, endereco) VALUES (?, ?);", nome_Tabela);

        try (PreparedStatement pstmt = conexao_DB.prepareStatement(query)) {
            pstmt.setString(1, nome_aluno);
            pstmt.setString(2, endereco);
            pstmt.executeUpdate();
            System.out.println("Linha inserida com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao inserir linha do Aluno: " + e.getMessage());
        }
    }

    public void inserir_linha_Professor(Connection conexao_DB, String nome_Tabela, String nome_professor, String endereco) {
        String query = String.format("INSERT INTO %s(nome_professor, endereco) VALUES (?, ?);", nome_Tabela);

        try (PreparedStatement pstmt = conexao_DB.prepareStatement(query)) {
            pstmt.setString(1, nome_professor);
            pstmt.setString(2, endereco);
            pstmt.executeUpdate();
            System.out.println("Linha inserida com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao inserir linha do Professor: " + e.getMessage());
        }
    }

    public void inserir_linha_Curso(Connection conexao_DB, String nome_Tabela, String nome_curso) {
        String query = String.format("INSERT INTO %s(nome_curso) VALUES (?);", nome_Tabela);

        try (PreparedStatement pstmt = conexao_DB.prepareStatement(query)) {
            pstmt.setString(1, nome_curso);
            pstmt.executeUpdate();
            System.out.println("Linha inserida com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao inserir linha do Curso: " + e.getMessage());
        }
    }

    public void inserir_linha_Disciplina(Connection conexao_DB, String nome_Tabela, String nome_disciplina) {
        String query = String.format("INSERT INTO %s(nome_disciplina) VALUES (?);", nome_Tabela);

        try (PreparedStatement pstmt = conexao_DB.prepareStatement(query)) {
            pstmt.setString(1, nome_disciplina);
            pstmt.executeUpdate();
            System.out.println("Linha inserida com sucesso na tabela Disciplina!");
        } catch (Exception e) {
            System.out.println("Erro ao inserir linha na tabela Disciplina: " + e.getMessage());
        }
    }

    public void inserir_linha_Turma(Connection conexao_DB, String nome_Tabela, String nome_turma, String nome_curso) {
        String queryCurso = "SELECT cod_curso FROM Curso WHERE nome_curso = ?";

        try (PreparedStatement pstmtCurso = conexao_DB.prepareStatement(queryCurso)) {
            pstmtCurso.setString(1, nome_curso);
            ResultSet rs = pstmtCurso.executeQuery();

            if (rs.next()) {
                int cod_curso = rs.getInt("cod_curso");

                String queryTurma = String.format("INSERT INTO %s(nome_turma, cod_curso) VALUES (?, ?);", nome_Tabela);

                try (PreparedStatement pstmtTurma = conexao_DB.prepareStatement(queryTurma)) {
                    pstmtTurma.setString(1, nome_turma);
                    pstmtTurma.setInt(2, cod_curso);
                    pstmtTurma.executeUpdate();
                    System.out.println("Linha inserida com sucesso na tabela Turma!");
                }
            } else {
                System.out.println("Curso não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao inserir linha na tabela Turma: " + e.getMessage());
        }
    }

    public void inserir_linha_Avaliacao(Connection conexao_DB, String nome_Tabela, int matricula, int cod_disciplina, double nota) {
        String queryAluno = "SELECT matricula FROM Aluno WHERE matricula = ?";
        String queryDisciplina = "SELECT cod_disciplina FROM Disciplina WHERE cod_disciplina = ?";

        try (PreparedStatement pstmtAluno = conexao_DB.prepareStatement(queryAluno);
             PreparedStatement pstmtDisciplina = conexao_DB.prepareStatement(queryDisciplina)) {

            pstmtAluno.setInt(1, matricula);
            pstmtDisciplina.setInt(1, cod_disciplina);

            ResultSet rsAluno = pstmtAluno.executeQuery();
            ResultSet rsDisciplina = pstmtDisciplina.executeQuery();

            if (rsAluno.next() && rsDisciplina.next()) {
                String queryAvaliacao = String.format(
                        "INSERT INTO %s (matricula, cod_disciplina, nota) VALUES (?, ?, ?);",
                        nome_Tabela
                );

                try (PreparedStatement pstmtAvaliacao = conexao_DB.prepareStatement(queryAvaliacao)) {
                    pstmtAvaliacao.setInt(1, matricula);
                    pstmtAvaliacao.setInt(2, cod_disciplina);
                    pstmtAvaliacao.setBigDecimal(3, BigDecimal.valueOf(nota));
                    pstmtAvaliacao.executeUpdate();
                    System.out.println("Linha inserida com sucesso na tabela Avaliacao!");
                }
            } else {
                if (!rsAluno.next()) {
                    System.out.println("Aluno não encontrado.");
                }
                if (!rsDisciplina.next()) {
                    System.out.println("Disciplina não encontrada.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao inserir linha na tabela Avaliacao: " + e.getMessage());
        }
    }

    public void inserir_linha_TurmaDisciplina(Connection conexao_DB, int cod_disciplina, int cod_turma){
        String queryCheck = "SELECT COUNT(*) FROM Turma_Disciplina WHERE cod_disciplina = ? AND cod_turma = ?";
        String queryInsert = "INSERT INTO Turma_Disciplina (cod_disciplina, cod_turma) VALUES (?, ?)";

        try (PreparedStatement pstmtCheck = conexao_DB.prepareStatement(queryCheck)) {
            pstmtCheck.setInt(1, cod_disciplina);
            pstmtCheck.setInt(2, cod_turma);

            ResultSet rsCheck = pstmtCheck.executeQuery();
            rsCheck.next();
            int count = rsCheck.getInt(1);

            if (count == 0) {
                try (PreparedStatement pstmtInsert = conexao_DB.prepareStatement(queryInsert)) {
                    pstmtInsert.setInt(1, cod_disciplina);
                    pstmtInsert.setInt(2, cod_turma);
                    pstmtInsert.executeUpdate();
                    System.out.println("Linha inserida com sucesso na tabela Turma_Disciplina!");
                }
            } else {
                System.out.println("Erro: A combinação de cod_disciplina e cod_turma já existe.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao inserir linha na tabela Turma_Disciplina: " + e.getMessage());
        }
    }
    public void ler_dados(Connection conexao_DB, String nome_Tabela) {
        String query = String.format("SELECT * FROM %s", nome_Tabela);

        try (Statement stmt = conexao_DB.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int numColunas = metaData.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= numColunas; i++) {
                    System.out.print(metaData.getColumnName(i) + ": " + rs.getString(i) + " ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler dados da Tabela: " + e.getMessage());
        }
    }

    public void atualizar_dado(Connection conexao_DB, String nome_Tabela, String coluna_alvo, String valor_novo, String coluna_chave, int chave) {
        String query = String.format("UPDATE %s SET %s = ? WHERE %s = ?", nome_Tabela, coluna_alvo, coluna_chave);

        try (PreparedStatement pstmt = conexao_DB.prepareStatement(query)) {
            pstmt.setString(1, valor_novo);
            pstmt.setInt(2, chave);
            pstmt.executeUpdate();
            System.out.println("Dado atualizado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar dados na Tabela: " + e.getMessage());
        }
    }

    public void pesquisar_por_coluna(Connection conexao_DB, String nome_Tabela, String coluna_pesquisa, int valor_pesquisa) {
        String query = String.format("SELECT * FROM %s WHERE %s = ?", nome_Tabela, coluna_pesquisa);

        try (PreparedStatement pstmt = conexao_DB.prepareStatement(query)) {
            pstmt.setInt(1, valor_pesquisa); // Definir diretamente como inteiro
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int numColunas = metaData.getColumnCount();

                for (int i = 1; i <= numColunas; i++) {
                    System.out.print(metaData.getColumnName(i) + ": " + rs.getString(i) + " ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Erro ao pesquisar na Tabela: " + e.getMessage());
        }
    }

    public void deletar_linha_por_chave(Connection conexao_DB, String nome_Tabela, String coluna_chave, int chave) {
        String query = String.format("DELETE FROM %s WHERE %s = ?", nome_Tabela, coluna_chave);

        try (PreparedStatement pstmt = conexao_DB.prepareStatement(query)) {
            pstmt.setInt(1, chave);
            pstmt.executeUpdate();
            System.out.println("Linha deletada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar linha: " + e.getMessage());
        }
    }

    public void deletar_tabela(Connection conexao_DB, String nome_Tabela) {
        String query = String.format("DROP TABLE %s", nome_Tabela);

        try (Statement stmt = conexao_DB.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("Tabela deletada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar tabela: " + e.getMessage());
        }
    }
    public void listarAlunosPorCurso(Connection conexao_DB, String nomeCurso) {
        String query = "SELECT a.nome_aluno, c.nome_curso " +
                "FROM Aluno a " +
                "JOIN Avaliacao av ON a.matricula = av.matricula " +
                "JOIN Disciplina d ON av.cod_disciplina = d.cod_disciplina " +
                "JOIN Turma_Disciplina td ON d.cod_disciplina = td.cod_disciplina " +
                "JOIN Turma t ON td.cod_turma = t.cod_turma " +
                "JOIN Curso c ON t.cod_curso = c.cod_curso " +
                "WHERE c.nome_curso = ?";

        try (PreparedStatement pstmt = conexao_DB.prepareStatement(query)) {
            pstmt.setString(1, nomeCurso);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("Aluno: " + rs.getString("nome_aluno") + ", Curso: " + rs.getString("nome_curso"));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar alunos por curso: " + e.getMessage());
        }
    }
    public void criarViewAlunosAprovados(Connection conexao_DB) {
        String queryCriarView = "CREATE OR REPLACE VIEW AlunosAprovados AS " +
                "SELECT a.nome_aluno, a.matricula " +
                "FROM Aluno a " +
                "JOIN Avaliacao av ON a.matricula = av.matricula " +
                "GROUP BY a.nome_aluno, a.matricula " +
                "HAVING AVG(av.nota) >= 7"; // Critério de aprovação (média >= 7)

        try (Statement stmt = conexao_DB.createStatement()) {
            stmt.executeUpdate(queryCriarView);
            System.out.println("View AlunosAprovados criada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar a View AlunosAprovados: " + e.getMessage());
        }
    }

    public void exibirAlunosAprovados(Connection conexao_DB) {
        String queryConsultaView = "SELECT * FROM AlunosAprovados";

        try (Statement stmt = conexao_DB.createStatement();
             ResultSet rs = stmt.executeQuery(queryConsultaView)) {

            System.out.println("\nAlunos Aprovados:");
            System.out.println("-----------------");

            while (rs.next()) {
                String nomeAluno = rs.getString("nome_aluno");
                int matricula = rs.getInt("matricula");
                System.out.println("Nome: " + nomeAluno + ", Matrícula: " + matricula);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao exibir alunos aprovados: " + e.getMessage());
        }
    }

}
