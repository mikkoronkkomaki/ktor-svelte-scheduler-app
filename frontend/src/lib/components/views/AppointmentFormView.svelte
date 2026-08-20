<script lang="ts">
  import { onMount } from 'svelte';
  import { appointmentsApi, type Appointment, type AppointmentStatus } from '$lib/api';
  import DatetimeInput from '$lib/components/DatetimeInput.svelte';

  let items: Appointment[] = [];
  let loading = true;
  let error = '';

  let description = '';
  let startTime = '';
  let endTime = '';
  let status: AppointmentStatus = 'reserved';

  async function loadAppointments() {
    loading = true;
    error = '';
    try {
      items = await appointmentsApi.list();
    } catch (e) {
      error = e instanceof Error ? e.message : 'Tuntematon virhe';
    } finally {
      loading = false;
    }
  }

  async function createAppointment() {
    error = '';
    try {
      await appointmentsApi.create({ description, startTime, endTime, status });
      description = '';
      startTime = '';
      endTime = '';
      status = 'reserved';
      await loadAppointments();
    } catch (e) {
      error = e instanceof Error ? e.message : 'Luonti epäonnistui';
    }
  }

  onMount(loadAppointments);
</script>

<section class="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
    <h2 class="mb-4 text-lg font-semibold text-slate-900">Varaus: luonti / muokkaus</h2>

    {#if error}
        <div class="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
        </div>
    {/if}

    <form on:submit|preventDefault={createAppointment} class="mb-8 grid gap-4">
        <div>
            <label class="mb-1 block text-sm font-medium text-slate-700" for="description">Kuvaus</label>
            <input
                    id="description"
                    bind:value={description}
                    placeholder="Esim. Hiustenleikkaus"
                    required
                    class="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none ring-indigo-200 transition focus:border-indigo-500 focus:ring"
            />
        </div>

        <div class="grid gap-4 md:grid-cols-2">
            <DatetimeInput bind:time={startTime} title="Alkuaika" />
            <DatetimeInput bind:time={endTime} title="Loppuaika" />
        </div>

        <div>
            <label class="mb-1 block text-sm font-medium text-slate-700" for="status">Status</label>
            <select
                    id="status"
                    bind:value={status}
                    class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-slate-900 outline-none ring-indigo-200 transition focus:border-indigo-500 focus:ring"
            >
                <option value="reserved">reserved</option>
                <option value="cancelled">cancelled</option>
                <option value="done">done</option>
                <option value="no-show">no-show</option>
            </select>
        </div>

        <button
                type="submit"
                class="inline-flex items-center justify-center rounded-lg bg-indigo-600 px-4 py-2 font-medium text-white transition hover:bg-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:ring-offset-2"
        >
            Luo ajanvaraus
        </button>
    </form>

    <section class="rounded-xl border border-slate-200 bg-slate-50 p-4">
        <h3 class="mb-3 text-base font-semibold text-slate-900">Lista</h3>

        {#if loading}
            <p class="text-slate-600">Ladataan...</p>
        {:else if items.length === 0}
            <p class="text-slate-600">Ei ajanvarauksia vielä.</p>
        {:else}
            <ul class="space-y-3">
                {#each items as item}
                    <li class="rounded-lg border border-slate-200 bg-white p-3">
                        <div class="flex items-start justify-between gap-3">
                            <strong class="text-slate-900">{item.description}</strong>
                            <span class="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-medium text-slate-700">
								{item.status}
							</span>
                        </div>
                        <p class="mt-1 text-sm text-slate-600">{item.startTime} - {item.endTime}</p>
                    </li>
                {/each}
            </ul>
        {/if}
    </section>
</section>