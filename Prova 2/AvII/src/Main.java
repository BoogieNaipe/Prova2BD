import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        FuncoesDB db = new FuncoesDB();
        Connection conexao_DB = db.conectar_DB("Escola", "postgres", "admin");

        db.criar_tabela(conexao_DB);

        db.inserir_linha_Aluno(conexao_DB, "Aluno", "Abilidebob Exemplo dos Santos", "Rua dos MataCavalos, 123");
        db.inserir_linha_Aluno(conexao_DB, "Aluno", "Maria Souza", "Rua das Flores, 456");
        db.inserir_linha_Aluno(conexao_DB, "Aluno", "João Silva", "Avenida Principal, 789");

        db.inserir_linha_Professor(conexao_DB, "Professor", "Maestro Silva Mendes", "Rua dos Pintassilgos, 987");
        db.inserir_linha_Professor(conexao_DB, "Professor", "Ana Oliveira", "Rua dos Ipês, 101");
        db.inserir_linha_Professor(conexao_DB, "Professor", "Pedro Santos", "Travessa da Paz, 22");

        db.inserir_linha_Curso(conexao_DB, "Curso", "Engenharia de Medicina");
        db.inserir_linha_Curso(conexao_DB, "Curso", "Ciência da Computação");
        db.inserir_linha_Curso(conexao_DB, "Curso", "Engenharia Civil");

        db.inserir_linha_Disciplina(conexao_DB, "Disciplina", "Montagem de Galos Ciborgues para Rinha Tridimensional");
        db.inserir_linha_Disciplina(conexao_DB, "Disciplina", "Algoritmos e Estruturas de Dados");
        db.inserir_linha_Disciplina(conexao_DB, "Disciplina", "Cálculo Diferencial e Integral");

        db.inserir_linha_Turma(conexao_DB, "Turma", "Quinta Série", "Engenharia de Medicina");
        db.inserir_linha_Turma(conexao_DB, "Turma", "Turma A", "Ciência da Computação");
        db.inserir_linha_Turma(conexao_DB, "Turma", "Turma B", "Engenharia Civil");

        db.inserir_linha_Avaliacao(conexao_DB, "Avaliacao", 1, 1, 10.0);
        db.inserir_linha_Avaliacao(conexao_DB, "Avaliacao", 2, 1, 9.5); // Maria Souza em Algoritmos e Estruturas de Dados
        db.inserir_linha_Avaliacao(conexao_DB, "Avaliacao", 3, 2, 7.0); // João Silva em Cálculo Diferencial e Integral

        db.inserir_linha_TurmaDisciplina(conexao_DB, 1, 1);


        db.atualizar_dado(conexao_DB, "Professor", "nome_professor","Francisco Exec dos Santos", "cod_professor",5);

        db.deletar_linha_por_chave(conexao_DB, "Aluno", "matricula", 3);

        db.deletar_tabela(conexao_DB, "Aluno");

        db.ler_dados(conexao_DB, "Professor");

        db.pesquisar_por_coluna(conexao_DB, "Professor", "cod_professor", 2);

        db.listarAlunosPorCurso(conexao_DB,"Engenharia de Medicina");

        db.criarViewAlunosAprovados(conexao_DB);

        db.exibirAlunosAprovados(conexao_DB);
    }


}