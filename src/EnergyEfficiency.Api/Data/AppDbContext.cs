using EnergyEfficiency.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EnergyEfficiency.Api.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<Setor> Setores => Set<Setor>();
    public DbSet<Equipamento> Equipamentos => Set<Equipamento>();
    public DbSet<LeituraConsumo> Leituras => Set<LeituraConsumo>();
    public DbSet<AlertaConsumo> Alertas => Set<AlertaConsumo>();
    public DbSet<Usuario> Usuarios => Set<Usuario>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<Setor>(e =>
        {
            e.ToTable("SETOR");
            e.HasKey(x => x.Id);
            e.Property(x => x.LimiteConsumoMensalKwh).HasPrecision(12, 2);
            e.HasIndex(x => x.Nome).IsUnique();
        });

        modelBuilder.Entity<Equipamento>(e =>
        {
            e.ToTable("EQUIPAMENTO");
            e.HasKey(x => x.Id);
            e.Property(x => x.PotenciaNominalWatts).HasPrecision(12, 2);
            e.HasIndex(x => x.NumeroSerie).IsUnique();
            e.HasOne(x => x.Setor)
             .WithMany(s => s.Equipamentos)
             .HasForeignKey(x => x.SetorId)
             .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<LeituraConsumo>(e =>
        {
            e.ToTable("LEITURA_CONSUMO");
            e.HasKey(x => x.Id);
            e.Property(x => x.ConsumoKwh).HasPrecision(12, 4);
            e.HasIndex(x => new { x.EquipamentoId, x.DataHora });
            e.HasOne(x => x.Equipamento)
             .WithMany(eq => eq.Leituras)
             .HasForeignKey(x => x.EquipamentoId)
             .OnDelete(DeleteBehavior.Cascade);
        });

        modelBuilder.Entity<AlertaConsumo>(e =>
        {
            e.ToTable("ALERTA_CONSUMO");
            e.HasKey(x => x.Id);
            e.Property(x => x.ConsumoAcumuladoKwh).HasPrecision(12, 2);
            e.Property(x => x.LimiteKwh).HasPrecision(12, 2);
            e.HasIndex(x => new { x.SetorId, x.Resolvido });
            e.HasOne(x => x.Setor)
             .WithMany(s => s.Alertas)
             .HasForeignKey(x => x.SetorId)
             .OnDelete(DeleteBehavior.Cascade);
        });

        modelBuilder.Entity<Usuario>(e =>
        {
            e.ToTable("USUARIO");
            e.HasKey(x => x.Id);
            e.HasIndex(x => x.Email).IsUnique();
        });
    }
}
