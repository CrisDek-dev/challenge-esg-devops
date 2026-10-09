using System.ComponentModel.DataAnnotations;

namespace EnergyEfficiency.Api.Models;

public class Usuario
{
    public int Id { get; set; }

    [Required]
    [StringLength(150)]
    public string Nome { get; set; } = string.Empty;

    [Required]
    [EmailAddress]
    [StringLength(150)]
    public string Email { get; set; } = string.Empty;

    [Required]
    [StringLength(255)]
    public string SenhaHash { get; set; } = string.Empty;

    [Required]
    [StringLength(20)]
    public string Papel { get; set; } = "OPERADOR";
}
