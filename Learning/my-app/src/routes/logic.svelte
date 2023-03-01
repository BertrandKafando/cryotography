<script>
    let students = [
        { id: 1, name: "Aziz", age: 25 },
        { id: 2, name: "Bertrand", age: 26 },
        { id: 3, name: "Cyril", age: 27 },
    ];

    function removefirt() {
        students = students.slice(1);
    }

    // async function

    async function getStudents() {
        const response = await fetch(
            "https://mdn.github.io/learning-area/javascript/oojs/json/superheroes.json"
        );
        if (!response.ok) throw new Error(response.statusText);
        const data = await response.json();
        return data;
    }

    let promise = getStudents();

    function handClick() {
        promise = getStudents();
    }
</script>

<div>
    <h1>Students</h1>
    <ul>
        {#each students as student (student.id)}
            <li>{student.id}: {student.name}</li>
        {/each}
    </ul>

    <button on:click={removefirt}>Remove first</button>

    <h1>Superheroes</h1>
    <button on:click={handClick}>Get Superheroes</button>
    <ul>
        {#await promise}
            <li>...Loading</li>
        {:then data}
            {#each data.members as member (member.name)}
                <li>{member.name}</li>
            {/each}
        {:catch error}
            <li>{error.message}</li>
        {/await}
    </ul>
</div>
