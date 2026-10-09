using EnergyEfficiency.Api.Services;
using EnergyEfficiency.Api.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace EnergyEfficiency.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]
public class AlertasController : ControllerBase
{
    private readonly IAlertaService _service;

    public AlertasController(IAlertaService service) => _service = service;

    /// <summary>
    /// Lista alertas de consumo com paginacao e filtros (requer autenticacao).
    /// </summary>
    [HttpGet]
    [ProducesResponseType(typeof(PagedResult<AlertaResponse>), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<ActionResult<PagedResult<AlertaResponse>>> Listar([FromQuery] AlertaFilter filtro, CancellationToken ct)
    {
        var result = await _service.ListarAsync(filtro, ct);
        return Ok(result);
    }

    /// <summary>
    /// Marca um alerta como resolvido (requer perfil ADMIN).
    /// </summary>
    [HttpPatch("{id:long}/resolver")]
    [Authorize(Roles = "ADMIN")]
    [ProducesResponseType(typeof(AlertaResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<AlertaResponse>> Resolver(long id, CancellationToken ct)
    {
        var result = await _service.ResolverAsync(id, ct);
        return Ok(result);
    }
}
