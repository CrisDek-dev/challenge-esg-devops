using EnergyEfficiency.Api.Models;

namespace EnergyEfficiency.Api.Data;

public static class DbSeeder
{
    public static async Task SeedAsync(AppDbContext db)
    {
        if (!db.Usuarios.Any())
        {
            db.Usuarios.Add(new Usuario
            {
                Nome = "Administrador",
                Email = "admin@energia.com",
                SenhaHash = BCrypt.Net.BCrypt.HashPassword("admin@123"),
                Papel = "ADMIN"
            });
        }

        if (!db.Setores.Any())
        {
            var producao = new Setor { Nome = "Producao", Descricao = "Linha de producao industrial", LimiteConsumoMensalKwh = 50000m };
            var escritorio = new Setor { Nome = "Escritorio", Descricao = "Area administrativa", LimiteConsumoMensalKwh = 8000m };
            var armazem = new Setor { Nome = "Armazem", Descricao = "Logistica e estoque", LimiteConsumoMensalKwh = 15000m };

            db.Setores.AddRange(producao, escritorio, armazem);
            await db.SaveChangesAsync();

            db.Equipamentos.AddRange(
                new Equipamento { Nome = "Compressor 01", NumeroSerie = "CMP-001", SetorId = producao.Id, PotenciaNominalWatts = 15000m },
                new Equipamento { Nome = "Esteira 01", NumeroSerie = "EST-001", SetorId = producao.Id, PotenciaNominalWatts = 7500m },
                new Equipamento { Nome = "Ar condicionado central", NumeroSerie = "AC-CENTRAL-001", SetorId = escritorio.Id, PotenciaNominalWatts = 9000m },
                new Equipamento { Nome = "Empilhadeira eletrica", NumeroSerie = "EMP-001", SetorId = armazem.Id, PotenciaNominalWatts = 5000m }
            );
        }

        await db.SaveChangesAsync();
    }
}
