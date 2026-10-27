"""Gera o resumo dos testes BDD (Cucumber) em Markdown para o "Summary" do GitHub Actions.

Uso: python3 scripts/resumo-testes-bdd.py testes-automatizados/target/cucumber.json >> "$GITHUB_STEP_SUMMARY"
"""
import json
import sys


def status_do_cenario(cenario):
    etapas = cenario.get("before", []) + cenario.get("steps", []) + cenario.get("after", [])
    situacoes = [etapa.get("result", {}).get("status") for etapa in etapas]
    if all(s == "passed" for s in situacoes):
        return "passed"
    if any(s == "failed" for s in situacoes):
        return "failed"
    return "skipped"


def main(caminho):
    sys.stdout.reconfigure(encoding="utf-8")
    try:
        with open(caminho, encoding="utf-8") as arquivo:
            funcionalidades = json.load(arquivo)
    except FileNotFoundError:
        print("## Testes BDD (Cucumber)\n\nRelatorio nao encontrado: os testes nao chegaram a executar.")
        return

    icones = {"passed": "✅", "failed": "❌", "skipped": "⏭️"}
    linhas = []
    contagem = {"passed": 0, "failed": 0, "skipped": 0}
    for funcionalidade in funcionalidades:
        for cenario in funcionalidade.get("elements", []):
            if cenario.get("type") != "scenario":
                continue
            status = status_do_cenario(cenario)
            contagem[status] += 1
            linhas.append(f"| {icones[status]} | {funcionalidade['name']} | {cenario['name']} |")

    total = sum(contagem.values())
    print("## Testes BDD (Cucumber + Rest Assured)\n")
    print(f"**{total} cenários** executados: {contagem['passed']} passaram, "
          f"{contagem['failed']} falharam, {contagem['skipped']} não executados.\n")
    print("| | Funcionalidade | Cenário |")
    print("|---|---|---|")
    print("\n".join(linhas))
    print("\nRelatório HTML completo no artefato **relatorio-testes-bdd** desta execução.")


if __name__ == "__main__":
    main(sys.argv[1] if len(sys.argv) > 1 else "testes-automatizados/target/cucumber.json")
